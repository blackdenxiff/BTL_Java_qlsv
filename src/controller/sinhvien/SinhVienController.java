package controller.sinhvien;

import dao.nconnguoidao.SinhVienDAO;
import dao.quanlydao.DangKiDAO;
import dao.quanlydao.LopHocPhanDAO;
import database.KNDatabase;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.nconnguoi.SinhVien;
import model.nhethong.UserSession;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SinhVienController {

    // --- CÁC VÙNG GIAO DIỆN ---
    @FXML private VBox viewDashboard, viewTKB, viewDiem, viewLopMo, viewTaiChinh;
    @FXML private Label lblHoTen, lblTienDo, lblCongNoSV;

    // --- GIAO DIỆN LỊCH MỚI (GRID CALENDAR) ---
    @FXML private GridPane gridTKB;
    @FXML private Label lblWeekRange;
    private LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);

    // --- BẢNG ĐIỂM ---
    @FXML private TableView<KetQuaHocTap> tblDiem;
    @FXML private TableColumn<KetQuaHocTap, String> colDiemTenMH, colDiemChu;
    @FXML private TableColumn<KetQuaHocTap, Integer> colSoTC;
    @FXML private TableColumn<KetQuaHocTap, Double> colDiemSo;

    // --- BẢNG ĐĂNG KÝ TRỰC TUYẾN ---
    @FXML private TextField txtMaLHPReg;
    @FXML private TableView<LichHoc> tblLopMo;
    @FXML private TableColumn<LichHoc, String> colMoMaLHP, colMoTenMH, colMoCa, colMoPhong;

    // --- BẢNG TÀI CHÍNH ---
    @FXML private TableView<ChiTietHocPhiSV> tblTaiChinh;
    @FXML private TableColumn<ChiTietHocPhiSV, String> colTCMaMH, colTCTenMH, colTCDonGia, colTCThanhTien;
    @FXML private TableColumn<ChiTietHocPhiSV, Integer> colTCSoTC;

    // --- KHU VỰC QR THANH TOÁN ---
    @FXML private ImageView imgQRHocPhi;
    @FXML private Label lblStatusQR, lblNoiDungQR;
    @FXML private ProgressIndicator pgLoadingQR;
    @FXML private Button btnTaoQR;
    @FXML private TextField txtSoTienNop;

    private long soTienNoHienTai = 0;
    private long soTienDangGiaoDich = 0;
    private String noiDungCheckQR;
    private Timer qrTimer;
    private final String API_TOKEN = "TIXWPMQCOKK1LX3SABCN9GN3AET4ELAPVPJUGDGNEMJHGPFSIBT8MMUOKYQB6WZ2";
    private final String STK_NHAN = "0362907044";

    private DangKiDAO dangKiDAO = new DangKiDAO();
    private LopHocPhanDAO lopHocPhanDAO = new LopHocPhanDAO();
    private SinhVienDAO sinhVienDAO = new SinhVienDAO();

    private String maSVHienTai;
    private java.text.NumberFormat currencyFormat = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
    private ObservableList<LichHoc> masterListLopMo = FXCollections.observableArrayList();
    private FilteredList<LichHoc> filteredLopMo;

    @FXML
    public void initialize() {
        String username = UserSession.getUsername();
        maSVHienTai = username;
        String sqlLayMaSV = "SELECT MaSV FROM NguoiDung WHERE Username = ?";
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlLayMaSV)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getString("MaSV") != null) maSVHienTai = rs.getString("MaSV").trim();
        } catch (SQLException e) {}

        setupTableColumns();
        SinhVien sv = sinhVienDAO.getSinhVienByMa(maSVHienTai);
        if (sv != null) lblHoTen.setText(sv.getHoDem() + " " + sv.getTen());
        else lblHoTen.setText(username);

        setupSearchLopMo();
        showDashboardView();
    }

    // =========================================================
    // ================= LOGIC THỜI KHÓA BIỂU ==================
    // =========================================================

    @FXML public void handleShowTKB() {
        hideAllViews();
        viewTKB.setVisible(true);
        renderGridTKB();
    }

    @FXML void handlePrevWeek(ActionEvent event) { startOfWeek = startOfWeek.minusWeeks(1); renderGridTKB(); }
    @FXML void handleNextWeek(ActionEvent event) { startOfWeek = startOfWeek.plusWeeks(1); renderGridTKB(); }

    private void renderGridTKB() {
        gridTKB.getChildren().clear();
        gridTKB.getColumnConstraints().clear();
        gridTKB.getRowConstraints().clear();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM");
        lblWeekRange.setText(startOfWeek.format(dtf) + " - " + startOfWeek.plusDays(6).format(dtf));

        // 1. Dòng tiêu đề Thứ (Bé lại)
        RowConstraints headerRow = new RowConstraints();
        headerRow.setPrefHeight(35);
        gridTKB.getRowConstraints().add(headerRow);

        String[] thu = {"CA/THỨ", "THỨ 2", "THỨ 3", "THỨ 4", "THỨ 5", "THỨ 6", "THỨ 7", "CN"};
        for (int i = 0; i < 8; i++) {
            VBox head = new VBox(0); head.setAlignment(Pos.CENTER);
            head.setStyle("-fx-background-color: #293e73; -fx-border-color: #3d5185; -fx-border-width: 0.5;");
            Label l1 = new Label(thu[i]); l1.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
            head.getChildren().add(l1);
            if (i > 0) {
                Label l2 = new Label(startOfWeek.plusDays(i-1).format(dtf));
                l2.setStyle("-fx-text-fill: #fdb913; -fx-font-size: 9px;");
                head.getChildren().add(l2);
            }
            gridTKB.add(head, i, 0);
            ColumnConstraints cc = new ColumnConstraints(); cc.setPercentWidth(i == 0 ? 8 : 13.1);
            gridTKB.getColumnConstraints().add(cc);
        }

        // 2. Dòng Ca học (To bằng nhau)
        for (int ca = 1; ca <= 4; ca++) {
            RowConstraints rc = new RowConstraints(); rc.setPercentHeight(22); rc.setVgrow(Priority.ALWAYS);
            gridTKB.getRowConstraints().add(rc);
            Label lblCa = new Label("CA " + ca); lblCa.setAlignment(Pos.CENTER); lblCa.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            lblCa.setStyle("-fx-background-color: #f8f9fa; -fx-font-weight: bold; -fx-border-color: #dcdde1; -fx-font-size: 11px;");
            gridTKB.add(lblCa, 0, ca);
        }

        // 3. Đổ môn học
        for (Map<String, Object> row : lopHocPhanDAO.getThoiKhoaBieuSinhVien(maSVHienTai, 0, 0)) {
            String caHocStr = (String) row.get("caHoc");
            int d = extractDayNum(caHocStr); int s = extractShiftNum(caHocStr);
            if (d >= 2 && d <= 8 && s >= 1 && s <= 4) {
                VBox card = new VBox(5); card.setAlignment(Pos.CENTER);
                card.setStyle("-fx-background-color: #3498db; -fx-background-radius: 5; -fx-padding: 5; -fx-margin: 2;");
                Label t1 = new Label((String) row.get("tenMH")); t1.setWrapText(true); t1.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
                t1.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px;");
                Label t2 = new Label("P: " + row.get("phongHoc")); t2.setStyle("-fx-text-fill: #ebf5fb; -fx-font-size: 9px;");
                card.getChildren().addAll(t1, t2);
                gridTKB.add(card, (d==8?7:d-1), s);
            }
        }
    }

    private int extractDayNum(String s) {
        if (s == null) return 0; s = s.split("-")[0];
        if (s.contains("CN") || s.contains("Chủ") || s.contains("8")) return 8;
        for (int i = 2; i <= 7; i++) if (s.contains(String.valueOf(i))) return i;
        return 2;
    }

    private int extractShiftNum(String s) {
        if (s == null || !s.contains("-")) return 1; s = s.split("-")[1];
        for (int i = 1; i <= 4; i++) if (s.contains(String.valueOf(i))) return i;
        return 1;
    }

    // =========================================================
    // ================= LOGIC TÀI CHÍNH & NỘP TIỀN ============
    // =========================================================

    private void loadDuLieuTaiChinh() {
        ObservableList<ChiTietHocPhiSV> listHocPhi = FXCollections.observableArrayList();
        long tongPhatSinh = 0;
        String sql = "SELECT mh.MaMH, mh.TenMH, mh.SoTC, qd.DonGiaTinChi, dk.TrangThai FROM DangKy dk " +
                "JOIN LopHocPhan lhp ON dk.MaLHP = lhp.MaLHP JOIN MonHoc mh ON lhp.MaMH = mh.MaMH " +
                "LEFT JOIN QuyDinhHocPhi qd ON CAST(lhp.NamHoc AS NVARCHAR(20)) = qd.NamHoc AND lhp.HocKy = qd.HocKy WHERE dk.MaSV = ?";
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSVHienTai); ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String tt = rs.getString("TrangThai"); if (tt == null) tt = "Đang học";
                long dg = rs.getLong("DonGiaTinChi"); if (dg <= 0) dg = 450000;
                long thanhTien = rs.getInt("SoTC") * dg;
                tongPhatSinh += thanhTien; // CỘNG DỒN ĐỂ KHÔNG BỊ 0 VNĐ
                listHocPhi.add(new ChiTietHocPhiSV(rs.getString("MaMH"), rs.getString("TenMH") + " (" + tt.trim() + ")",
                        rs.getInt("SoTC"), currencyFormat.format(dg), currencyFormat.format(thanhTien)));
            }
        } catch (SQLException e) {}
        tblTaiChinh.setItems(listHocPhi);

        long daNop = 0;
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT SUM(SoTienThu) FROM PhieuThu WHERE MaSV = ?")) {
            ps.setString(1, maSVHienTai); ResultSet rs = ps.executeQuery(); if (rs.next()) daNop = rs.getLong(1);
        } catch (SQLException e) {}

        this.soTienNoHienTai = tongPhatSinh - daNop;
        if (soTienNoHienTai > 0) {
            lblCongNoSV.setText(currencyFormat.format(soTienNoHienTai) + " VNĐ");
            lblCongNoSV.setStyle("-fx-text-fill: #e74c3c;");
            txtSoTienNop.setText(String.valueOf(soTienNoHienTai)); txtSoTienNop.setDisable(false); btnTaoQR.setDisable(false);
        } else {
            lblCongNoSV.setText("ĐÃ HOÀN THÀNH HỌC PHÍ");
            lblCongNoSV.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            txtSoTienNop.clear(); txtSoTienNop.setDisable(true); btnTaoQR.setDisable(true);
        }
    }

    private void paySuccess() {
        if (qrTimer != null) qrTimer.cancel();
        String sql1 = "INSERT INTO PhieuThu (MaSV, NgayThu, SoTienThu, NoiDung, MaNhanVien) VALUES (?, GETDATE(), ?, ?, 'QR_AUTO')";
        String sql2 = "UPDATE DangKy SET TrangThai = N'Đã đóng tiền' WHERE MaSV = ? AND (TrangThai = N'Đã đăng ký' OR TrangThai = N'Đang học')";
        try (Connection conn = KNDatabase.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sql1); PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                ps1.setString(1, maSVHienTai); ps1.setLong(2, soTienDangGiaoDich); ps1.setString(3, "Nộp HP online: " + noiDungCheckQR); ps1.executeUpdate();
                ps2.setString(1, maSVHienTai); ps2.executeUpdate();
                conn.commit();
                Platform.runLater(() -> {
                    imgQRHocPhi.setImage(null); pgLoadingQR.setVisible(false);
                    lblStatusQR.setText("✅ XÁC NHẬN: ĐÃ ĐÓNG TIỀN THÀNH CÔNG");
                    loadDuLieuTaiChinh(); renderGridTKB();
                });
            } catch (SQLException e) { conn.rollback(); }
        } catch (SQLException e) {}
    }

    // =========================================================
    // ================= LOGIC ĐĂNG KÝ & RÚT MÔN ================
    // =========================================================

    @FXML public void handleActionRutMon() {
        String maLHP = txtMaLHPReg.getText().trim();
        if (maLHP.isEmpty()) return;
        // Kiểm tra xem đã nộp tiền chưa
        if (dangKiDAO.checkDaNopTien(maSVHienTai, maLHP)) {
            showAlert("Không thể rút", "Môn này đã đóng tiền. Hệ thống đã khóa không cho phép rút!");
            return;
        }
        dangKiDAO.deleteDangKy(maSVHienTai, maLHP);
        showAlert("Thành công", "Đã rút môn: " + maLHP);
        txtMaLHPReg.clear(); renderGridTKB(); loadDuLieuTaiChinh();
    }

    @FXML public void handleActionDangKy() {
        String maLHP = txtMaLHPReg.getText().trim(); if (maLHP.isEmpty() || dangKiDAO.checkDaDangKy(maSVHienTai, maLHP)) return;
        String caGoc = ""; try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT CaHoc FROM LopHocPhan WHERE MaLHP = ?")) {
            ps.setString(1, maLHP); ResultSet rs = ps.executeQuery(); if (rs.next()) caGoc = rs.getString(1).trim();
        } catch (SQLException e) {}
        String newCa = dangKiDAO.findAndAutoFixSchedule(maSVHienTai, caGoc);
        if (newCa.equals("FULL")) { showAlert("Lỗi", "Kín lịch!"); return; }
        if (!newCa.equals(caGoc)) dangKiDAO.updateCaHocLopHocPhan(maLHP, newCa);
        dangKiDAO.insertDangKy(new model.quanly.DangKi(maSVHienTai, maLHP, 0.0, "Đang học"));
        txtMaLHPReg.clear(); renderGridTKB(); loadDuLieuTaiChinh(); loadDanhSachLopMo();
    }

    // --- CÁC HÀM CƠ BẢN KHÁC (GIỮ NGUYÊN) ---
    private void hideAllViews() { viewDashboard.setVisible(false); viewTKB.setVisible(false); viewDiem.setVisible(false); viewLopMo.setVisible(false); viewTaiChinh.setVisible(false); }
    @FXML public void showDashboardView() { hideAllViews(); viewDashboard.setVisible(true); lblTienDo.setText(dangKiDAO.tongTinChiSinhVienDangKy(maSVHienTai) + " / 120 Tín chỉ"); }
    @FXML public void handleShowDiem() { hideAllViews(); viewDiem.setVisible(true); loadDuLieuDiem(); }
    @FXML public void handleShowLopMo() { hideAllViews(); viewLopMo.setVisible(true); loadDanhSachLopMo(); }
    @FXML public void handleShowTaiChinh() { hideAllViews(); viewTaiChinh.setVisible(true); loadDuLieuTaiChinh(); }
    @FXML public void handleTaoQRTaiCho() {
        if (soTienNoHienTai <= 0) return;
        try { String val = txtSoTienNop.getText().replaceAll("[^\\d]", ""); if (val.isEmpty()) return; soTienDangGiaoDich = Long.parseLong(val); } catch (Exception e) { return; }
        noiDungCheckQR = "HP" + maSVHienTai + "R" + ((int)(Math.random()*90000)+10000);
        imgQRHocPhi.setImage(new Image("https://img.vietqr.io/image/MB-" + STK_NHAN + "-compact.png?amount=" + soTienDangGiaoDich + "&addInfo=" + noiDungCheckQR));
        pgLoadingQR.setVisible(true); btnTaoQR.setDisable(true); txtSoTienNop.setDisable(true);
        if (qrTimer != null) qrTimer.cancel(); qrTimer = new Timer();
        qrTimer.scheduleAtFixedRate(new TimerTask() { @Override public void run() { checkBank(); } }, 2000, 3000);
    }
    private void checkBank() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create("https://my.sepay.vn/userapi/transactions/list?account_number=" + STK_NHAN)).header("Authorization", "Bearer " + API_TOKEN).GET().build();
            if (client.send(req, HttpResponse.BodyHandlers.ofString()).body().contains(noiDungCheckQR)) Platform.runLater(this::paySuccess);
        } catch (Exception e) {}
    }
    private void setupTableColumns() {
        colDiemTenMH.setCellValueFactory(cell -> cell.getValue().tenMHProperty()); colSoTC.setCellValueFactory(cell -> cell.getValue().soTCProperty().asObject()); colDiemSo.setCellValueFactory(cell -> cell.getValue().diemSoProperty().asObject()); colDiemChu.setCellValueFactory(cell -> cell.getValue().diemChuProperty());
        colMoMaLHP.setCellValueFactory(cell -> cell.getValue().maLHPProperty()); colMoTenMH.setCellValueFactory(cell -> cell.getValue().tenMHProperty()); colMoCa.setCellValueFactory(cell -> cell.getValue().caHocProperty()); colMoPhong.setCellValueFactory(cell -> cell.getValue().phongProperty());
        colTCMaMH.setCellValueFactory(cell -> cell.getValue().maMHProperty()); colTCTenMH.setCellValueFactory(cell -> cell.getValue().tenMHProperty()); colTCSoTC.setCellValueFactory(cell -> cell.getValue().soTCProperty().asObject()); colTCDonGia.setCellValueFactory(cell -> cell.getValue().donGiaProperty()); colTCThanhTien.setCellValueFactory(cell -> cell.getValue().thanhTienProperty());
    }
    private void loadDuLieuDiem() {
        ObservableList<KetQuaHocTap> list = FXCollections.observableArrayList();
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT mh.TenMH, mh.SoTC, dk.DiemSo FROM DangKy dk JOIN LopHocPhan lhp ON dk.MaLHP = lhp.MaLHP JOIN MonHoc mh ON lhp.MaMH = mh.MaMH WHERE dk.MaSV = ?")) {
            ps.setString(1, maSVHienTai); ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new KetQuaHocTap(rs.getString("TenMH"), rs.getInt("SoTC"), rs.getDouble("DiemSo")));
        } catch (SQLException e) {}
        tblDiem.setItems(list);
    }
    private void loadDanhSachLopMo() {
        masterListLopMo.clear();
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT lhp.MaLHP, mh.TenMH, lhp.CaHoc, lhp.PhongHoc FROM LopHocPhan lhp INNER JOIN MonHoc mh ON lhp.MaMH = mh.MaMH")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) masterListLopMo.add(new LichHoc(rs.getString("MaLHP"), rs.getString("TenMH"), rs.getString("CaHoc"), rs.getString("PhongHoc")));
        } catch (SQLException e) {}
    }
    private void setupSearchLopMo() {
        filteredLopMo = new FilteredList<>(masterListLopMo, p -> true);
        txtMaLHPReg.textProperty().addListener((obs, old, newVal) -> filteredLopMo.setPredicate(l -> newVal == null || newVal.isEmpty() || l.getMaLHP().toLowerCase().contains(newVal.toLowerCase()) || l.getTenMH().toLowerCase().contains(newVal.toLowerCase())));
        tblLopMo.setItems(filteredLopMo);
        tblLopMo.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> { if (newVal != null) txtMaLHPReg.setText(newVal.getMaLHP()); });
    }
    @FXML private void handleShowProfile(javafx.scene.input.MouseEvent event) { SinhVien sv = sinhVienDAO.getSinhVienByMa(maSVHienTai); if (sv != null) showAlert("Hồ sơ", sv.getHoDem() + " " + sv.getTen() + "\nMSV: " + maSVHienTai); }
    @FXML private void handleLogout(ActionEvent event) { try { UserSession.clear(); ((Stage)((Node)event.getSource()).getScene().getWindow()).close(); Parent r = FXMLLoader.load(getClass().getResource("/view/securityview/Login.fxml")); Stage s = new Stage(); s.setScene(new Scene(r)); s.show(); } catch (IOException e) {}}
    @FXML private void handleXuatTKB() { showAlert("Thông báo", "Đang xuất PDF..."); }
    @FXML private void handleShowTinTuc() { showDashboardView(); showAlert("Tin tức", "Thông báo mới hiển thị tại Trang chủ!"); }
    @FXML private void handleChangePassword(ActionEvent event) { try { Parent root = FXMLLoader.load(getClass().getResource("/view/securityview/ChangePassword.fxml")); Stage stage = new Stage(); stage.setScene(new Scene(root)); stage.initModality(Modality.APPLICATION_MODAL); stage.showAndWait(); } catch (IOException e) {} }
    private void showAlert(String t, String c) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(t); a.setHeaderText(null); a.setContentText(c); a.showAndWait(); }

    public static class KetQuaHocTap {
        private final SimpleStringProperty tenMH, diemChu; private final SimpleIntegerProperty soTC; private final SimpleDoubleProperty diemSo;
        public KetQuaHocTap(String t, int s, double d) { this.tenMH = new SimpleStringProperty(t); this.soTC = new SimpleIntegerProperty(s); this.diemSo = new SimpleDoubleProperty(d); this.diemChu = new SimpleStringProperty(d>=8.5?"A":d>=7?"B":d>=5.5?"C":d>=4?"D":"F"); }
        public SimpleStringProperty tenMHProperty() { return tenMH; } public SimpleIntegerProperty soTCProperty() { return soTC; } public SimpleDoubleProperty diemSoProperty() { return diemSo; } public SimpleStringProperty diemChuProperty() { return diemChu; }
    }
    public static class LichHoc {
        private final SimpleStringProperty maLHP, tenMH, caHoc, phong;
        public LichHoc(String m, String t, String c, String p) { this.maLHP = new SimpleStringProperty(m); this.tenMH = new SimpleStringProperty(t); this.caHoc = new SimpleStringProperty(c); this.phong = new SimpleStringProperty(p); }
        public String getMaLHP() { return maLHP.get(); } public String getTenMH() { return tenMH.get(); } public SimpleStringProperty maLHPProperty() { return maLHP; } public SimpleStringProperty tenMHProperty() { return tenMH; } public SimpleStringProperty caHocProperty() { return caHoc; } public SimpleStringProperty phongProperty() { return phong; }
    }
    public static class ChiTietHocPhiSV {
        private final SimpleStringProperty maMH, tenMH, donGia, thanhTien; private final SimpleIntegerProperty soTC;
        public ChiTietHocPhiSV(String m, String t, int s, String d, String tt) { this.maMH = new SimpleStringProperty(m); this.tenMH = new SimpleStringProperty(t); this.soTC = new SimpleIntegerProperty(s); this.donGia = new SimpleStringProperty(d); this.thanhTien = new SimpleStringProperty(tt); }
        public SimpleStringProperty maMHProperty() { return maMH; } public SimpleStringProperty tenMHProperty() { return tenMH; } public SimpleIntegerProperty soTCProperty() { return soTC; } public SimpleStringProperty donGiaProperty() { return donGia; } public SimpleStringProperty thanhTienProperty() { return thanhTien; }
    }
}