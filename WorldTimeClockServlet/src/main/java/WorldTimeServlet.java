import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class WorldTimeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String timeZoneId = request.getParameter("timezone");
        TimeZone tz;
        String message;

        if (timeZoneId != null && !timeZoneId.trim().isEmpty()) {
            tz = TimeZone.getTimeZone(timeZoneId);
            if ("GMT".equals(tz.getID()) && !"GMT".equalsIgnoreCase(timeZoneId)) {
                message = "Invalid time zone";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                sdf.setTimeZone(tz);
                message = "The current time in " + tz.getID() + " is " + sdf.format(System.currentTimeMillis());
            }
        } else {
            message = "No timezone specified. Please enter a time zone.";
        }

        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>World Time Clock</title></head>");
        out.println("<body>");
        out.println("<p>" + message + "</p>");
        out.println("<form action='index.html'>");
        out.println("<input type='submit' value='Back' />");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }
}
