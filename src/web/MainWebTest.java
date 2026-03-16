package web;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
public class MainWebTest {
    public static void main(String[] args) {
        try {
            // Đường dẫn đến file HTML của bạn
            File htmlFile = new File("src/web/test.html");

            if (htmlFile.exists()) {
                // Lệnh này sẽ mở file bằng trình duyệt mặc định của máy tính
                Desktop.getDesktop().browse(htmlFile.toURI());
                System.out.println("Đang mở file HTML trên trình duyệt...");
            } else {
                System.out.println("Không tìm thấy file index.html. Bạn hãy tạo file đó trước nhé!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
