package org.example.webapp;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HelloServlet extends HttpServlet {

    public static final String TRACK_VISIT_SQL = "INSERT INTO VISITS(WHEN) VALUES(CURRENT_TIMESTAMP)";
    public static final String LIST_VISITS_SQL = "SELECT * FROM (SELECT WHEN FROM VISITS ORDER BY ID DESC) WHERE ROWNUM <= 10";

    private final DataSourceProvider dataSourceProvider;
    private DataSource dataSource;

    public HelloServlet() {
        dataSourceProvider = new JndiDataSourceProvider();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        PrintWriter pw = response.getWriter();
        pw.println("<html><head><title>JBoss &amp; DB</title></head><body>");

        DataSource ds = getDataSource(pw);
        if (ds == null)
            return;

        trackAndPrintVisits(pw, ds);
        pw.println("</body>");
    }

    private synchronized DataSource getDataSource(PrintWriter pw) {
        if (dataSource == null) {
            try {
                dataSource = dataSourceProvider.getDataSource();
            } catch (Exception e) {
                pw.println("Could not obtain datasource");
                e.printStackTrace(pw);
            }
        }
        return dataSource;
    }


    private void trackAndPrintVisits(PrintWriter pw, DataSource ds) {
        try (Connection conn = ds.getConnection()) {
            trackVisit(pw, conn);
            printVisits(pw, conn);
        } catch (SQLException e) {
            pw.println("Could not obtain a connection!<br/>");
            e.printStackTrace(pw);
            pw.println("<br/>");
        }
    }

    private void printVisits(PrintWriter pw, Connection conn) {
        pw.println("<h2>Latest visits</h2>");
        try (Statement stmt = conn.createStatement()) {
            try (ResultSet resultSet = stmt.executeQuery(LIST_VISITS_SQL)) {
                pw.println("<table><thead><th>#</th><th>Time</th></thead></tbody>");
                int rowCount = 1;
                while (resultSet.next()) {
                    pw.printf("<tr><td>%d</td><td>%s</td></tr>%n", rowCount++, resultSet.getString(1));
                }
                pw.println("</tbody></table>");
            }
        } catch (SQLException e) {
            pw.println("Could not list visits!<br/>");
            e.printStackTrace(pw);
            pw.println("<br/>");
        }

    }

    private void trackVisit(PrintWriter pw, Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(TRACK_VISIT_SQL);
            pw.println("Your visit was tracked<br/>");
        } catch (SQLException e) {
            pw.println("Could not track your visit!<br/>");
            e.printStackTrace(pw);
            pw.println("<br/>");
        }
    }
}