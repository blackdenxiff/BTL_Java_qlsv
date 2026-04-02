package controller.adminc;

import dao.nconnguoidao.SinhVienDAO;
import dao.quanlydao.DangKiDAO;
import dao.quanlydao.HocPhiDAO;
import database.KNDatabase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.nconnguoi.SinhVien;
import model.nhethong.UserSession;
import model.quanly.PhieuThu; // Sử dụng model PhieuThu đã tách riêng

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FinanceManagerController {

    // --- CẤU HÌNH BẢNG CHI TIẾT HỌC PHÍ ---
    @FXML private TextField txtMaSV;
    @FXML private TableView<ChiTietHocPhi> tableHocPhi;
    @FXML private TableColumn<ChiTietHocPhi, String> colMaMH, colTenMH, colDonGia, colThanhTien, colTrangThaiHocPhan;
    @FXML private TableColumn<ChiTietHocPhi, Integer> colSoTC;

    // --- CẤU HÌNH BẢNG LỊCH SỬ NỘP TIỀN ---
    @FXML private TableView<PhieuThu> tableLichSuThu; //
    @FXML private TableColumn<PhieuThu, String> colNgayThu, colNguoiThu, colSoTienNop;

    // --- CÁC NHÃN THÔNG TIN SINH VIÊN ---
    @FXML private Label lblTenSV, lblLop, lblTrangThai, lblKhoa, lblCTDT, lblCoVan;

    // --- CÁC NHÃN TỔNG KẾT TÀI CHÍNH ---
    @FXML private Label lblTongTien, lblDaNop, lblConNo, lblTieuDeNo;
    @FXML private TextField txtTienThu;

    private ObservableList<ChiTietHocPhi> listHocPhi = FXCollections.observableArrayList();
    private ObservableList<PhieuThu> listLichSu = FXCollections.observableArrayList();

    private long tongTienNo = 0;
    private long donGiaHienTai = 0;

    private HocPhiDAO hocPhiDAO = new HocPhiDAO(); //
    private SinhVienDAO sinhVienDAO = new SinhVienDAO(); //
    private DangKiDAO dangKiDAO = new DangKiDAO(); //
    private NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {
        setupTableHocPhi();
        setupTableLichSu();

        // Chỉ cho phép nhập số vào ô tiền thu
        txtTienThu.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) txtTienThu.setText(newVal.replaceAll("[^\\d]", ""));
        });

        // Lấy đơn giá mới nhất từ hệ thống để làm mốc dự phòng
        donGiaHienTai = hocPhiDAO.getDonGiaHienTai();
    }

    private void setupTableHocPhi() {
        colMaMH.setCellValueFactory(cell -> cell.getValue().maMHProperty());
        colTenMH.setCellValueFactory(cell -> cell.getValue().tenMHProperty());
        colSoTC.setCellValueFactory(cell -> cell.getValue().soTCProperty().asObject());
        colDonGia.setCellValueFactory(cell -> cell.getValue().donGiaStringProperty());
        colThanhTien.setCellValueFactory(cell -> cell.getValue().thanhTienStringProperty());
        colTrangThaiHocPhan.setCellValueFactory(cell -> cell.getValue().trangThaiProperty());

        // Định dạng màu sắc trực quan cho trạng thái từng môn
        colTrangThaiHocPhan.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Đã thanh toán")) setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    else if (item.contains("Chưa đóng")) setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    else setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                }
            }
        });
        tableHocPhi.setItems(listHocPhi);
    }

    private void setupTableLichSu() {
        colNgayThu.setCellValueFactory(cell -> cell.getValue().ngayThuProperty());
        colSoTienNop.setCellValueFactory(cell -> cell.getValue().soTienStringProperty());
        colNguoiThu.setCellValueFactory(cell -> cell.getValue().nguoiThuProperty());
        tableLichSuThu.setItems(listLichSu);
    }

    @FXML
    private void handleSearchStudent() {
        String maSV = txtMaSV.getText().trim();
        if (maSV.isEmpty()) {
            showAlert("Cảnh báo", "Vui lòng nhập Mã Sinh Viên!", Alert.AlertType.WARNING);
            return;
        }

        SinhVien sv = sinhVienDAO.getSinhVienByMa(maSV); //
        if (sv == null) {
            showAlert("Lỗi", "Không tìm thấy sinh viên có mã: " + maSV, Alert.AlertType.ERROR);
            clearForm();
            return;
        }

        // 1. Cập nhật thông tin sinh viên lên giao diện
        lblTenSV.setText(sv.getHoDem() + " " + sv.getTen());
        lblLop.setText(sv.getMaLop());
        lblTrangThai.setText(sv.getTrangThaiSV() != null ? sv.getTrangThaiSV() : "Chưa cập nhật");

        Map<String, String> info = getThongTinHocTapChiTiet(maSV);
        lblKhoa.setText(info.getOrDefault("Khoa", "..."));
        lblCTDT.setText(info.getOrDefault("CTDT", "..."));
        lblCoVan.setText(info.getOrDefault("CoVan", "..."));

        // 2. Tải lịch sử nộp tiền cũ
        loadLichSuNopTien(maSV);

        // 3. Logic "Rót tiền": Phân bổ quỹ tiền mặt vào từng môn học
        listHocPhi.clear();
        long tongDaNop = layTongTienDaNop(maSV);
        long quyTienHienCo = tongDaNop;
        long tongPhatSinh = 0;

        // Lấy danh sách môn học (đã dùng LEFT JOIN trong DAO để hiện đầy đủ)
        List<Map<String, Object>> danhSachMon = dangKiDAO.getChiTietBangDiem(maSV);

        for (Map<String, Object> row : danhSachMon) {
            String maMH = (String) row.get("maMH");
            String tenMH = (String) row.get("tenMH");
            int soTC = (int) row.get("soTinChi");
            // Lấy đúng đơn giá của kỳ đó từ lịch sử quy định học phí
            long giaMon = (row.get("donGiaApDung") != null) ? ((Number) row.get("donGiaApDung")).longValue() : donGiaHienTai;

            long thanhTienMon = soTC * giaMon;
            tongPhatSinh += thanhTienMon;

            String tinhTrang;
            if (quyTienHienCo >= thanhTienMon) {
                tinhTrang = "Đã thanh toán";
                quyTienHienCo -= thanhTienMon;
            } else if (quyTienHienCo > 0) {
                tinhTrang = " Thiếu " + currencyFormat.format(thanhTienMon - quyTienHienCo) + " đ";
                quyTienHienCo = 0;
            } else {
                tinhTrang = "Chưa đóng";
            }
            listHocPhi.add(new ChiTietHocPhi(maMH, tenMH, soTC, giaMon, tinhTrang));
        }

        // 4. Cập nhật tổng kết tài chính
        tongTienNo = tongPhatSinh - tongDaNop;
        lblTongTien.setText(currencyFormat.format(tongPhatSinh) + " đ");
        lblDaNop.setText(currencyFormat.format(tongDaNop) + " đ");
        updateFinancialStatusLabels();
    }

    private void loadLichSuNopTien(String maSV) {
        listLichSu.clear();
        String sql = "SELECT CONVERT(VARCHAR, NgayThu, 103) + ' ' + CONVERT(VARCHAR, NgayThu, 108) as NgayFull, " +
                "SoTienThu, NguoiThu FROM PhieuThu WHERE MaSV = ? ORDER BY NgayThu DESC";
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long soTienRaw = rs.getLong("SoTienThu");
                listLichSu.add(new PhieuThu(
                        rs.getString("NgayFull"),
                        soTienRaw,
                        rs.getString("NguoiThu"),
                        currencyFormat.format(soTienRaw) + " đ"
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleCollectMoney() {
        if (tongTienNo <= 0) {
            showAlert("Thông báo", "Sinh viên đã hoàn tất học phí.", Alert.AlertType.INFORMATION);
            return;
        }

        String tienStr = txtTienThu.getText().trim();
        if (tienStr.isEmpty()) {
            showAlert("Cảnh báo", "Vui lòng nhập số tiền thu!", Alert.AlertType.WARNING);
            return;
        }

        long soTienThu = Long.parseLong(tienStr);
        if (soTienThu <= 0 || soTienThu > tongTienNo) {
            showAlert("Lỗi", "Số tiền không hợp lệ hoặc vượt quá dư nợ!", Alert.AlertType.ERROR);
            return;
        }

        if (luuPhieuThu(txtMaSV.getText().trim(), soTienThu)) {
            showAlert("Thành công", "Đã ghi nhận thanh toán " + currencyFormat.format(soTienThu) + " đ", Alert.AlertType.INFORMATION);
            handleSearchStudent(); // Tự động refresh dữ liệu và lịch sử nộp
        }
    }

    private boolean luuPhieuThu(String maSV, long soTien) {
        // Sử dụng dấu ? cho NguoiThu thay vì ghi chết 'ADMIN'
        String sql = "INSERT INTO PhieuThu (MaSV, SoTienThu, NgayThu, NguoiThu) VALUES (?, ?, GETDATE(), ?)";

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSV);
            ps.setLong(2, soTien);

            // Lấy MaNV/Username từ Session đã lưu lúc đăng nhập
            String nguoiThu = UserSession.getUsername();
            ps.setString(3, (nguoiThu != null) ? nguoiThu : "ADMIN");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi lưu phiếu thu: " + e.getMessage());
            return false;
        }
    }

    private long layTongTienDaNop(String maSV) {
        long tong = 0;
        String sql = "SELECT SUM(SoTienThu) FROM PhieuThu WHERE MaSV = ?";
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) tong = rs.getLong(1);
        } catch (SQLException e) { }
        return tong;
    }

    private void updateFinancialStatusLabels() {
        if (tongTienNo > 0) {
            lblTieuDeNo.setText("CÒN NỢ:"); lblTieuDeNo.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            lblConNo.setText(currencyFormat.format(tongTienNo) + " đ"); lblConNo.setStyle("-fx-font-size: 22px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            txtTienThu.setText(String.valueOf(tongTienNo)); txtTienThu.setDisable(false);
        } else if (tongTienNo < 0) {
            lblTieuDeNo.setText("TIỀN DƯ:"); lblTieuDeNo.setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
            lblConNo.setText(currencyFormat.format(Math.abs(tongTienNo)) + " đ"); lblConNo.setStyle("-fx-font-size: 22px; -fx-text-fill: #2980b9; -fx-font-weight: bold;");
            txtTienThu.setText("0"); txtTienThu.setDisable(true);
        } else {
            lblTieuDeNo.setText("TRẠNG THÁI:"); lblTieuDeNo.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            lblConNo.setText("Đã nộp đủ"); lblConNo.setStyle("-fx-font-size: 22px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
            txtTienThu.setText("0"); txtTienThu.setDisable(true);
        }
    }

    private Map<String, String> getThongTinHocTapChiTiet(String maSV) {
        Map<String, String> info = new HashMap<>();
        String sql = "SELECT k.TenKhoa, c.TenCTDT, l.MaNV FROM SinhVien sv " +
                "LEFT JOIN LopHanhChinh l ON sv.MaLop = l.MaLop " +
                "LEFT JOIN ChuongTrinhDaoTao c ON l.MaCTDT = c.MaCTDT " +
                "LEFT JOIN Khoa k ON c.MaKhoa = k.MaKhoa WHERE sv.MaSV = ?";
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                info.put("Khoa", rs.getString(1));
                info.put("CTDT", rs.getString(2));
                info.put("CoVan", rs.getString(3));
            }
        } catch (SQLException e) { }
        return info;
    }

    @FXML private void handleConfigPrice() { /* Logic UPSERT đơn giá cũ của bạn */ }
    private void clearForm() {
        lblTenSV.setText("..."); lblLop.setText("..."); lblTongTien.setText("0 đ");
        lblDaNop.setText("0 đ"); lblConNo.setText("0 đ");
        listHocPhi.clear(); listLichSu.clear();
    }
    private void showAlert(String t, String c, Alert.AlertType at) {
        Alert a = new Alert(at); a.setTitle(t); a.setHeaderText(null); a.setContentText(c); a.showAndWait();
    }

    // --- INNER CLASS CHO BẢNG HỌC PHÍ ---
    public class ChiTietHocPhi {
        private final SimpleStringProperty maMH, tenMH, trangThai;
        private final SimpleIntegerProperty soTC;
        private final SimpleLongProperty donGia, thanhTien;

        public ChiTietHocPhi(String maMH, String tenMH, int soTC, long donGia, String trangThai) {
            this.maMH = new SimpleStringProperty(maMH); this.tenMH = new SimpleStringProperty(tenMH);
            this.soTC = new SimpleIntegerProperty(soTC); this.donGia = new SimpleLongProperty(donGia);
            this.thanhTien = new SimpleLongProperty(soTC * donGia); this.trangThai = new SimpleStringProperty(trangThai);
        }
        public SimpleStringProperty maMHProperty() { return maMH; }
        public SimpleStringProperty tenMHProperty() { return tenMH; }
        public SimpleIntegerProperty soTCProperty() { return soTC; }
        public SimpleStringProperty donGiaStringProperty() { return new SimpleStringProperty(currencyFormat.format(donGia.get())); }
        public SimpleStringProperty thanhTienStringProperty() { return new SimpleStringProperty(currencyFormat.format(thanhTien.get())); }
        public SimpleStringProperty trangThaiProperty() { return trangThai; }
    }
}