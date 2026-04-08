package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TestThanhToanController {

    @FXML private ImageView imgQR;
    @FXML private Label lblSoTien;
    @FXML private Label lblNoiDung;

    private final String API_TOKEN = "TIXWPMQCOKK1LX3SABCN9GN3AET4ELAPVPJUGDGNEMJHGPFSIBT8MMUOKYQB6WZ2";
    private final String STK = "0362907044";

    // TẠO MÃ NGẪU NHIÊN ĐỂ KHÔNG TRÙNG GIAO DỊCH CŨ
    private String maHoaDon;
    private double soTienCanTra = 10000;

    private Timer timer;
    private int messageIndex = 0;

    @FXML
    public void initialize() {
        // Tạo mã ngẫu nhiên mỗi lần mở App (Ví dụ: HD4829)
        Random rd = new Random();
        maHoaDon = "HD" + (rd.nextInt(9000) + 1000);

        hienThiGiaoDienBanDau();
        batDauQuetVaChayChu();
    }

    private void hienThiGiaoDienBanDau() {
        // Tạo mã QR với mã hóa đơn mới
        String urlQR = String.format("https://img.vietqr.io/image/MB-%s-compact.png?amount=%d&addInfo=%s",
                STK, (int)soTienCanTra, maHoaDon);

        imgQR.setImage(new Image(urlQR));
        lblSoTien.setText(String.format("%,.0f VNĐ", soTienCanTra));
    }

    private void batDauQuetVaChayChu() {
        String[] statusMessages = {
                "Đang chờ quét mã...",
                "Đang kiểm tra MB Bank...",
                "Nội dung bắt buộc: " + maHoaDon,
                "Đang quét giao dịch mới..."
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    lblNoiDung.setText(statusMessages[messageIndex]);
                    messageIndex = (messageIndex + 1) % statusMessages.length;
                });
                kiemTraGiaoDichThucTe();
            }
        }, 0, 3000);
    }

    private void kiemTraGiaoDichThucTe() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            // Lấy 5 giao dịch gần nhất để đảm bảo không sót
            String url = "https://my.sepay.vn/userapi/transactions/list?account_number=" + STK + "&limit=5";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();

                // Chỉ xác nhận nếu thấy đúng MÃ MỚI TẠO (maHoaDon)
                if (body.contains(maHoaDon)) {
                    Platform.runLater(() -> xuLyThanhToanThanhCong());
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi kết nối mạng...");
        }
    }

    private void xuLyThanhToanThanhCong() {
        if (timer != null) timer.cancel();
        imgQR.setImage(null);
        lblNoiDung.setText("✅ XÁC NHẬN: ĐÃ NHẬN TIỀN!");
        lblNoiDung.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 20; -fx-font-weight: bold;");
        lblSoTien.setText("GIAO DỊCH HOÀN TẤT");
    }
}