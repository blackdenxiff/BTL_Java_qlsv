package controller.nhanvien;

import dao.nconnguoidao.NhanVienDAO;
import model.nconnguoi.NhanVien;
import model.nhethong.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.net.URL;

public class HomeNVController {

    @FXML private VBox contentArea;
    @FXML private Accordion menuAccordion;
    @FXML private Label lblUsernameHeader;

    private NhanVienDAO nvDAO = new NhanVienDAO();

    @FXML
    public void initialize() {
        // Hiển thị tên đăng nhập lên header
        if (lblUsernameHeader != null) {
            lblUsernameHeader.setText("👤 " + UserSession.getUsername());
        }

        // Mở sẵn menu đầu tiên
        if (menuAccordion != null && !menuAccordion.getPanes().isEmpty()) {
            menuAccordion.setExpandedPane(menuAccordion.getPanes().get(0));
        }

        // Gọi hàm load trang chủ mặc định
        loadHome();
    }

    // ================= HÀM TRANG CHỦ (FIX LỖI CỦA BẠN) =================
    @FXML
    public void handleHome() {
        loadHome();
    }

    private void loadHome() {
        loadView("HomeContent.fxml");
    }

    // ================= CÁC HÀM ĐIỀU HƯỚNG MENU =================

    @FXML
    private void handleSinhVien() {
        loadView("UpdateSinhVien.fxml");
    }

    @FXML
    private void handleLopHocPhan() {
        loadView("LopHocPhan.fxml");
    }

    @FXML
    public void handleNhapDiem() {
        loadView("NhapDiem.fxml");
    }

    @FXML
    private void handleHocPhi() {
        // Load file từ thư mục adminview như đã thống nhất
        loadView("/view/adminview/FinanceManager.fxml");
    }

    // ================= XỬ LÝ HỒ SƠ & ĐỔI MK =================

    @FXML
    private void handleShowProfile() {
        String currentId = UserSession.getUsername();
        NhanVien nv = nvDAO.getNhanVienByMa(currentId);

        if (nv != null) {
            String thongTin = String.format(
                    "Mã Nhân viên: %s\nHọ và Tên: %s %s\nNgày sinh: %s\nChức vụ: %s\nEmail: %s",
                    nv.getMaNV(), nv.getHoDem(), nv.getTen(), nv.getNgaySinh(), nv.getLoaiNV(), nv.getEmail()
            );
            showAlert("Hồ sơ nhân viên", thongTin);
        }
    }

    @FXML
    private void handleChangePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/securityview/ChangePassword.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Đổi mật khẩu");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout() {
        try {
            UserSession.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/securityview/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ================= HÀM LOAD VIEW DÙNG CHUNG =================
    private void loadView(String path) {
        try {
            if (!path.startsWith("/")) {
                path = "/view/nhanvienview/" + path;
            }
            URL url = getClass().getResource(path);
            if (url == null) return;

            Parent view = FXMLLoader.load(url);
            VBox.setVgrow(view, Priority.ALWAYS); // Đảm bảo nội dung tràn viền
            contentArea.getChildren().setAll(view);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}