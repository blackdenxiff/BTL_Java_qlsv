package controller.sinhvien;

import dao.nconnguoidao.SinhVienDAO;
import dao.quanlydao.DangKiDAO;
import dao.quanlydao.LopHocPhanDAO;
import database.KNDatabase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.nconnguoi.SinhVien;
import model.nhethong.UserSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class SinhVienController {

    // --- CÁC VÙNG GIAO DIỆN ---
    @FXML private VBox viewDashboard;
    @FXML private VBox viewTKB;
    @FXML private VBox viewDiem;
    @FXML private VBox viewLopMo;
    @FXML private VBox viewTaiChinh;

    // --- THÔNG TIN HEADER & DASHBOARD ---
    @FXML private Label lblHoTen;
    @FXML private Label lblTienDo;
    @FXML private Label lblCongNoSV;

    // --- BẢNG TRA CỨU LỊCH (TKB) ---
    @FXML private TableView<LichHoc> tblTKB;
    @FXML private TableColumn<LichHoc, String> colMaLHP, colTenMH, colCaHoc, colPhong;

    // --- BẢNG ĐIỂM ---
    @FXML private TableView<KetQuaHocTap> tblDiem;
    @FXML private TableColumn<KetQuaHocTap, String> colDiemTenMH, colDiemChu;
    @FXML private TableColumn<KetQuaHocTap, Integer> colSoTC;
    @FXML private TableColumn<KetQuaHocTap, Double> colDiemSo;

    // --- BẢNG ĐĂNG KÝ TRỰC TUYẾN ---
    @FXML private TextField txtMaLHPReg;
    @FXML private TableView<LichHoc> tblLopMo;
    @FXML private TableColumn<LichHoc, String> colMoMaLHP, colMoTenMH, colMoCa, colMoPhong;

    // Biến lưu trữ cho Live Search Đăng ký trực tuyến
    private ObservableList<LichHoc> masterListLopMo = FXCollections.observableArrayList();
    private FilteredList<LichHoc> filteredLopMo;

    // --- BẢNG TÀI CHÍNH ---
    @FXML private TableView<ChiTietHocPhiSV> tblTaiChinh;
    @FXML private TableColumn<ChiTietHocPhiSV, String> colTCMaMH, colTCTenMH, colTCDonGia, colTCThanhTien;
    @FXML private TableColumn<ChiTietHocPhiSV, Integer> colTCSoTC;

    // --- KHAI BÁO CÁC DAO ---
    private DangKiDAO dangKiDAO = new DangKiDAO();
    private LopHocPhanDAO lopHocPhanDAO = new LopHocPhanDAO();
    private SinhVienDAO sinhVienDAO = new SinhVienDAO();

    private String maSVHienTai;
    private final int HOC_KY_HIEN_TAI = 2;
    private final int NAM_HOC_HIEN_TAI = 2024;
    private java.text.NumberFormat currencyFormat = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));

    @FXML
    public void initialize() {
        maSVHienTai = UserSession.getUsername();
        setupTableColumns();

        SinhVien sv = sinhVienDAO.getSinhVienByMa(maSVHienTai);
        if (sv != null) lblHoTen.setText(sv.getHoDem() + " " + sv.getTen());
        else lblHoTen.setText(maSVHienTai);

        setupSearchLopMo(); // Khởi tạo bộ máy Live Search
        showDashboardView(); // Mặc định mở Trang chủ
    }

    // =========================================================
    // =============== LOGIC CHUYỂN ĐỔI MENU ===================
    // =========================================================

    private void hideAllViews() {
        viewDashboard.setVisible(false);
        viewTKB.setVisible(false);
        viewDiem.setVisible(false);
        viewLopMo.setVisible(false);
        if (viewTaiChinh != null) viewTaiChinh.setVisible(false);
    }

    @FXML public void showDashboardView() {
        hideAllViews();
        viewDashboard.setVisible(true);
        int tongTC = dangKiDAO.tongTinChiSinhVienDangKy(maSVHienTai);
        lblTienDo.setText(tongTC + " / 120 Tín chỉ");
    }

    @FXML public void handleShowTKB() {
        hideAllViews(); viewTKB.setVisible(true); loadDuLieuTKB();
    }

    @FXML public void handleShowDiem() {
        hideAllViews(); viewDiem.setVisible(true); loadDuLieuDiem();
    }

    @FXML public void handleShowLopMo() {
        hideAllViews(); viewLopMo.setVisible(true); loadDanhSachLopMo();
    }

    @FXML public void handleShowTaiChinh() {
        hideAllViews(); viewTaiChinh.setVisible(true); loadDuLieuTaiChinh();
    }

    @FXML public void handleShowTinTuc() {
        showDashboardView();
        showAlert("Tin tức", "Các thông báo mới nhất được hiển thị tại bảng Trang chủ!");
    }

    // =========================================================
    // ================== LOGIC BẢNG & TẢI DỮ LIỆU =============
    // =========================================================

    private void setupTableColumns() {
        // Bảng Điểm
        colDiemTenMH.setCellValueFactory(cell -> cell.getValue().tenMHProperty());
        colSoTC.setCellValueFactory(cell -> cell.getValue().soTCProperty().asObject());
        colDiemSo.setCellValueFactory(cell -> cell.getValue().diemSoProperty().asObject());
        colDiemChu.setCellValueFactory(cell -> cell.getValue().diemChuProperty()); // Bật Điểm Chữ

        // Bảng TKB
        colMaLHP.setCellValueFactory(cell -> cell.getValue().maLHPProperty());
        colTenMH.setCellValueFactory(cell -> cell.getValue().tenMHProperty());
        colCaHoc.setCellValueFactory(cell -> cell.getValue().caHocProperty());
        colPhong.setCellValueFactory(cell -> cell.getValue().phongProperty());

        // Bảng Đăng ký Lớp Mở
        colMoMaLHP.setCellValueFactory(cell -> cell.getValue().maLHPProperty());
        colMoTenMH.setCellValueFactory(cell -> cell.getValue().tenMHProperty());
        colMoCa.setCellValueFactory(cell -> cell.getValue().caHocProperty());
        colMoPhong.setCellValueFactory(cell -> cell.getValue().phongProperty());

        // Bảng Tài Chính
        if (colTCMaMH != null) {
            colTCMaMH.setCellValueFactory(cell -> cell.getValue().maMHProperty());
            colTCTenMH.setCellValueFactory(cell -> cell.getValue().tenMHProperty());
            colTCSoTC.setCellValueFactory(cell -> cell.getValue().soTCProperty().asObject());
            colTCDonGia.setCellValueFactory(cell -> cell.getValue().donGiaProperty());
            colTCThanhTien.setCellValueFactory(cell -> cell.getValue().thanhTienProperty());
        }
    }

    private void loadDuLieuDiem() {
        ObservableList<KetQuaHocTap> listDiem = FXCollections.observableArrayList();
        String sql = "SELECT mh.TenMH, mh.SoTC, dk.DiemSo FROM DangKy dk " +
                "JOIN LopHocPhan lhp ON dk.MaLHP = lhp.MaLHP " +
                "JOIN MonHoc mh ON lhp.MaMH = mh.MaMH WHERE dk.MaSV = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSVHienTai);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listDiem.add(new KetQuaHocTap(rs.getString("TenMH"), rs.getInt("SoTC"), rs.getDouble("DiemSo")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        tblDiem.setItems(listDiem);
    }

    private void loadDuLieuTKB() {
        ObservableList<LichHoc> listTKB = FXCollections.observableArrayList();
        for (Map<String, Object> row : lopHocPhanDAO.getThoiKhoaBieuSinhVien(maSVHienTai, HOC_KY_HIEN_TAI, NAM_HOC_HIEN_TAI)) {
            listTKB.add(new LichHoc((String) row.get("maLHP"), (String) row.get("tenMH"), (String) row.get("caHoc"), (String) row.get("phongHoc")));
        }
        tblTKB.setItems(listTKB);
    }

    private void loadDanhSachLopMo() {
        // 1. Xóa dữ liệu cũ trên bảng
        masterListLopMo.clear();

        // 2. Viết truy vấn SQL trực tiếp để tránh lỗi sai tên cột ở file DAO
        String sql = "SELECT lhp.MaLHP, mh.TenMH, lhp.CaHoc, lhp.PhongHoc " +
                "FROM LopHocPhan lhp " +
                "INNER JOIN MonHoc mh ON lhp.MaMH = mh.MaMH " +
                "WHERE lhp.HocKy = ? AND lhp.NamHoc = ?";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, HOC_KY_HIEN_TAI);
            ps.setInt(2, NAM_HOC_HIEN_TAI);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // 3. Lấy dữ liệu và tự động gán rỗng nếu DB bị Null để tránh lỗi
                String maLHP = rs.getString("MaLHP") != null ? rs.getString("MaLHP") : "";
                String tenMH = rs.getString("TenMH") != null ? rs.getString("TenMH") : "";
                String caHoc = rs.getString("CaHoc") != null ? rs.getString("CaHoc") : "";
                String phong = rs.getString("PhongHoc") != null ? rs.getString("PhongHoc") : "";

                masterListLopMo.add(new LichHoc(maLHP, tenMH, caHoc, phong));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi nạp danh sách lớp mở: " + e.getMessage());
        }
    }

    private void setupSearchLopMo() {
        filteredLopMo = new javafx.collections.transformation.FilteredList<>(masterListLopMo, p -> true);

        // Lọc khi gõ phím (Đã thêm cơ chế CHỐNG LỖI NullPointerException)
        txtMaLHPReg.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredLopMo.setPredicate(lich -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();

                // Tránh lỗi nếu getMaLHP() hoặc getTenMH() trả về null
                String maLop = lich.getMaLHP() != null ? lich.getMaLHP().toLowerCase() : "";
                String tenMon = lich.getTenMH() != null ? lich.getTenMH().toLowerCase() : "";

                if (maLop.contains(lowerCaseFilter)) return true;
                else if (tenMon.contains(lowerCaseFilter)) return true;

                return false;
            });
        });

        javafx.collections.transformation.SortedList<LichHoc> sortedData = new javafx.collections.transformation.SortedList<>(filteredLopMo);
        sortedData.comparatorProperty().bind(tblLopMo.comparatorProperty());
        tblLopMo.setItems(sortedData);

        // Click 1 dòng sẽ tự nhảy mã lên ô Text
        tblLopMo.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && newSelection.getMaLHP() != null) {
                txtMaLHPReg.setText(newSelection.getMaLHP());
            }
        });
    }

    private void loadDuLieuTaiChinh() {
        ObservableList<ChiTietHocPhiSV> listHocPhi = FXCollections.observableArrayList();
        long tongPhatSinh = 0;

        for (Map<String, Object> row : dangKiDAO.getChiTietBangDiem(maSVHienTai)) {
            String maMH = (String) row.get("maMH");
            String tenMH = (String) row.get("tenMH");
            int soTC = (int) row.get("soTinChi");
            long donGia = ((Number) row.get("donGiaApDung")).longValue();

            if (donGia == 0) donGia = 400000; // Giá trị dự phòng
            long thanhTien = soTC * donGia;
            tongPhatSinh += thanhTien;

            listHocPhi.add(new ChiTietHocPhiSV(maMH, tenMH, soTC, currencyFormat.format(donGia), currencyFormat.format(thanhTien)));
        }
        tblTaiChinh.setItems(listHocPhi);

        long tongDaNop = 0;
        String sqlTienNop = "SELECT SUM(SoTienThu) FROM PhieuThu WHERE MaSV = ?";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlTienNop)) {
            ps.setString(1, maSVHienTai);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) tongDaNop = rs.getLong(1);
        } catch (SQLException e) {}

        long conNo = tongPhatSinh - tongDaNop;
        if (conNo > 0) {
            lblCongNoSV.setText(currencyFormat.format(conNo) + " VNĐ");
            lblCongNoSV.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        } else {
            lblCongNoSV.setText("Đã hoàn thành học phí");
            lblCongNoSV.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2ecc71;");
        }
    }

    // =========================================================
    // ================= THAO TÁC NÚT BẤM ======================
    // =========================================================

    @FXML public void handleActionDangKy() {
        String maLHP = txtMaLHPReg.getText().trim();
        if (maLHP.isEmpty()) { showAlert("Lỗi", "Vui lòng nhập Mã lớp học phần!"); return; }

        if (dangKiDAO.checkDaDangKy(maSVHienTai, maLHP)) {
            showAlert("Thông báo", "Bạn đã đăng ký lớp học phần này rồi!"); return;
        }

        model.quanly.DangKi dk = new model.quanly.DangKi(maSVHienTai, maLHP, 0.0, "Đang học");
        dangKiDAO.insertDangKy(dk);

        showAlert("Thành công", "Đăng ký thành công lớp: " + maLHP);
        txtMaLHPReg.clear();
        loadDuLieuTKB();
    }

    @FXML public void handleActionRutMon() {
        String maLHP = txtMaLHPReg.getText().trim();
        if (maLHP.isEmpty()) { showAlert("Lỗi", "Vui lòng nhập Mã lớp học phần cần rút!"); return; }

        dangKiDAO.deleteDangKy(maSVHienTai, maLHP);
        showAlert("Thành công", "Đã hủy đăng ký lớp: " + maLHP);
        txtMaLHPReg.clear();
        loadDuLieuTKB();
    }

    @FXML private void handleShowProfile(javafx.scene.input.MouseEvent event) {
        SinhVien sv = sinhVienDAO.getSinhVienByMa(maSVHienTai);
        if (sv != null) {
            String thongTin = String.format(
                    "Mã Sinh Viên: %s\nHọ và Tên: %s %s\nNgày sinh: %s\nGiới tính: %s\nLớp hành chính: %s\nEmail: %s",
                    sv.getMaSV(), sv.getHoDem(), sv.getTen(), sv.getNgaySinh(), sv.getGioiTinh(), sv.getMaLop(), sv.getEmail()
            );
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hồ sơ cá nhân"); alert.setHeaderText("Thông tin Sinh viên");
            alert.setContentText(thongTin); alert.showAndWait();
        }
    }

    @FXML private void handleXuatTKB() {
        showAlert("Thành công", "Chức năng xuất File đang được bảo trì.");
    }

    // TÍCH HỢP ĐỔI MẬT KHẨU TỪ FILE CỦA BẠN
    @FXML private void handleChangePassword(ActionEvent event) {
        try {
            // Mở cửa sổ Đổi mật khẩu như một Popup (Modal) sử dụng ChangePasswordController của bạn
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/securityview/ChangePassword.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Đổi mật khẩu bảo mật");
            stage.setScene(new Scene(root));
            // Ngăn người dùng click vào màn hình phía sau khi Popup đang mở
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải giao diện Đổi mật khẩu. Hãy kiểm tra đường dẫn file FXML!");
        }
    }

    @FXML private void handleLogout(ActionEvent event) {
        try {
            UserSession.clear();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            Parent root = FXMLLoader.load(getClass().getResource("/view/securityview/Login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Đăng nhập hệ thống");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null);
        alert.setContentText(content); alert.showAndWait();
    }

    // =========================================================
    // ======== CÁC INNER CLASSES ĐỂ HIỂN THỊ TRÊN BẢNG ========
    // =========================================================

    public static class KetQuaHocTap {
        private final SimpleStringProperty tenMH, diemChu;
        private final SimpleIntegerProperty soTC;
        private final SimpleDoubleProperty diemSo;

        public KetQuaHocTap(String tenMH, int soTC, double diemSo) {
            this.tenMH = new SimpleStringProperty(tenMH);
            this.soTC = new SimpleIntegerProperty(soTC);
            this.diemSo = new SimpleDoubleProperty(diemSo);
            this.diemChu = new SimpleStringProperty(quyDoiDiemChu(diemSo));
        }

        private static String quyDoiDiemChu(double diem) {
            if (diem >= 9.5) return "A+";
            if (diem >= 8.5) return "A";
            if (diem >= 8.0) return "B+";
            if (diem >= 7.0) return "B";
            if (diem >= 6.0) return "C";
            if (diem >= 5.0) return "D+";
            if (diem >= 4.0) return "D";
            return "F";
        }

        public SimpleStringProperty tenMHProperty() { return tenMH; }
        public SimpleIntegerProperty soTCProperty() { return soTC; }
        public SimpleDoubleProperty diemSoProperty() { return diemSo; }
        public SimpleStringProperty diemChuProperty() { return diemChu; }
    }

    // =========================================================
    // ======== CÁC INNER CLASSES ĐỂ HIỂN THỊ TRÊN BẢNG ========
    // =========================================================

    public static class LichHoc {
        private final SimpleStringProperty maLHP, tenMH, caHoc, phong;

        public LichHoc(String maLHP, String tenMH, String caHoc, String phong) {
            this.maLHP = new SimpleStringProperty(maLHP);
            this.tenMH = new SimpleStringProperty(tenMH);
            this.caHoc = new SimpleStringProperty(caHoc);
            this.phong = new SimpleStringProperty(phong);
        }

        // --- BỔ SUNG CÁC HÀM GETTER NÀY ĐỂ FIX LỖI CANNOT FIND SYMBOL ---
        public String getMaLHP() { return maLHP.get(); }
        public String getTenMH() { return tenMH.get(); }
        public String getCaHoc() { return caHoc.get(); }
        public String getPhong() { return phong.get(); }
        // ----------------------------------------------------------------

        public SimpleStringProperty maLHPProperty() { return maLHP; }
        public SimpleStringProperty tenMHProperty() { return tenMH; }
        public SimpleStringProperty caHocProperty() { return caHoc; }
        public SimpleStringProperty phongProperty() { return phong; }
    }

    public static class ChiTietHocPhiSV {
        private final SimpleStringProperty maMH, tenMH, donGia, thanhTien;
        private final SimpleIntegerProperty soTC;
        public ChiTietHocPhiSV(String maMH, String tenMH, int soTC, String donGia, String thanhTien) {
            this.maMH = new SimpleStringProperty(maMH); this.tenMH = new SimpleStringProperty(tenMH);
            this.soTC = new SimpleIntegerProperty(soTC); this.donGia = new SimpleStringProperty(donGia);
            this.thanhTien = new SimpleStringProperty(thanhTien);
        }
        public SimpleStringProperty maMHProperty() { return maMH; }
        public SimpleStringProperty tenMHProperty() { return tenMH; }
        public SimpleIntegerProperty soTCProperty() { return soTC; }
        public SimpleStringProperty donGiaProperty() { return donGia; }
        public SimpleStringProperty thanhTienProperty() { return thanhTien; }
    }
}