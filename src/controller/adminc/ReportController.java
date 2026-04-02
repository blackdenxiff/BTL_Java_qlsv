package controller.adminc;

import database.KNDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import model.quanly.BaoCaoDaoTao;
import model.quanly.BaoCaoHeThong;
import model.quanly.BaoCaoTaiChinh;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class ReportController {

    // === CÁC THÀNH PHẦN TAB 1 (TÀI CHÍNH) ===
    @FXML private Label lblTongDoanhThu;
    @FXML private BarChart<String, Number> chartDoanhThuKhoa;
    @FXML private TextField txtTimKiemTC;
    @FXML private ComboBox<String> cbLocTrangThaiTC;
    @FXML private TableView<BaoCaoTaiChinh> tableTaiChinh;
    @FXML private TableColumn<BaoCaoTaiChinh, String> colMaSV_TC, colHoTen_TC, colLop_TC;
    @FXML private TableColumn<BaoCaoTaiChinh, String> colTongPhatSinh_TC, colDaNop_TC, colConNo_TC;
    private ObservableList<BaoCaoTaiChinh> listTaiChinh = FXCollections.observableArrayList();
    private FilteredList<BaoCaoTaiChinh> filteredDataTC;

    // === CÁC THÀNH PHẦN TAB 2 (ĐÀO TẠO) ===
    @FXML private PieChart chartHocLuc;
    @FXML private TextField txtTimKiemDT;
    @FXML private ComboBox<String> cbLocHocLucDT;
    @FXML private TableView<BaoCaoDaoTao> tableDaoTao;
    @FXML private TableColumn<BaoCaoDaoTao, String> colMaSV_DT, colHoTen_DT, colTenMH_DT, colMaLHP_DT, colXepLoai_DT;
    @FXML private TableColumn<BaoCaoDaoTao, Number> colDiem_DT;
    private ObservableList<BaoCaoDaoTao> listDaoTao = FXCollections.observableArrayList();
    private FilteredList<BaoCaoDaoTao> filteredDataDT;

    // === CÁC THÀNH PHẦN TAB 3 (HỆ THỐNG) ===
    @FXML private BarChart<String, Number> chartMatDoSV;
    @FXML private TextField txtTimKiemHT;
    @FXML private ComboBox<String> cbLocKhoaHT;
    @FXML private TableView<BaoCaoHeThong> tableHeThong;
    @FXML private TableColumn<BaoCaoHeThong, String> colMaSV_HT, colHoTen_HT, colLop_HT, colKhoa_HT, colTrangThai_HT;
    private ObservableList<BaoCaoHeThong> listHeThong = FXCollections.observableArrayList();
    private FilteredList<BaoCaoHeThong> filteredDataHT;

    private NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {
        // Khởi tạo Tab 1
        loadDuLieuBieuDoTaiChinh();
        setupTableTaiChinh();
        loadDuLieuBangTaiChinh();

        // Khởi tạo Tab 2
        loadDuLieuBieuDoDaoTao();
        setupTableDaoTao();
        loadDuLieuBangDaoTao();

        // Khởi tạo Tab 3
        loadDuLieuBieuDoHeThong();
        setupTableHeThong();
        loadDuLieuBangHeThong();
    }

    // ==============================================================================
    // ============================ LOGIC TAB 1: TÀI CHÍNH ==========================
    // ==============================================================================
    private void loadDuLieuBieuDoTaiChinh() {
        String sqlTong = "SELECT SUM(SoTienThu) as Tong FROM PhieuThu";
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlTong)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) lblTongDoanhThu.setText(currencyFormat.format(rs.getLong("Tong")) + " đ");
        } catch (SQLException e) { e.printStackTrace(); }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        String sqlKhoa = "SELECT k.TenKhoa, SUM(pt.SoTienThu) as DoanhThu FROM PhieuThu pt " +
                "JOIN SinhVien sv ON pt.MaSV = sv.MaSV JOIN LopHanhChinh lhc ON sv.MaLop = lhc.MaLop " +
                "JOIN ChuongTrinhDaoTao ctdt ON lhc.MaCTDT = ctdt.MaCTDT JOIN Khoa k ON ctdt.MaKhoa = k.MaKhoa " +
                "GROUP BY k.TenKhoa";
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlKhoa)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) series.getData().add(new XYChart.Data<>(rs.getString("TenKhoa"), rs.getLong("DoanhThu")));
        } catch (SQLException e) { }
        chartDoanhThuKhoa.getData().add(series);
    }

    private void setupTableTaiChinh() {
        colMaSV_TC.setCellValueFactory(cell -> cell.getValue().maSVProperty());
        colHoTen_TC.setCellValueFactory(cell -> cell.getValue().hoTenProperty());
        colLop_TC.setCellValueFactory(cell -> cell.getValue().lopProperty());
        colTongPhatSinh_TC.setCellValueFactory(cell -> cell.getValue().tongPhatSinhStrProperty());
        colDaNop_TC.setCellValueFactory(cell -> cell.getValue().daNopStrProperty());
        colConNo_TC.setCellValueFactory(cell -> cell.getValue().conNoStrProperty());

        cbLocTrangThaiTC.getItems().addAll("Tất cả", "Chưa hoàn thành học phí", "Đã hoàn thành");
        cbLocTrangThaiTC.setValue("Tất cả");

        filteredDataTC = new FilteredList<>(listTaiChinh, p -> true);
        txtTimKiemTC.textProperty().addListener((obs, old, newVal) -> filterTaiChinh());
        cbLocTrangThaiTC.valueProperty().addListener((obs, old, newVal) -> filterTaiChinh());

        SortedList<BaoCaoTaiChinh> sortedData = new SortedList<>(filteredDataTC);
        sortedData.comparatorProperty().bind(tableTaiChinh.comparatorProperty());
        tableTaiChinh.setItems(sortedData);
    }

    private void filterTaiChinh() {
        String keyword = txtTimKiemTC.getText().toLowerCase().trim();
        String status = cbLocTrangThaiTC.getValue();
        filteredDataTC.setPredicate(bc -> {
            boolean matchKey = keyword.isEmpty() || bc.getMaSV().toLowerCase().contains(keyword) || bc.getHoTen().toLowerCase().contains(keyword);
            boolean matchStatus = true;
            if (status.equals("Chưa hoàn thành học phí")) matchStatus = bc.getConNo() > 0;
            else if (status.equals("Đã hoàn thành")) matchStatus = bc.getConNo() <= 0;
            return matchKey && matchStatus;
        });
    }

    private void loadDuLieuBangTaiChinh() {
        listTaiChinh.clear();
        String sql = "SELECT sv.MaSV, sv.HoDem + ' ' + sv.Ten AS HoTen, sv.MaLop, " +
                "ISNULL(TienHoc.TongPhatSinh, 0) AS TongPhatSinh, ISNULL(TienNop.DaNop, 0) AS DaNop " +
                "FROM SinhVien sv LEFT JOIN ( " +
                "   SELECT dk.MaSV, SUM(mh.SoTC * ISNULL(qd.DonGiaTinChi, 0)) AS TongPhatSinh " +
                "   FROM DangKy dk JOIN LopHocPhan lhp ON dk.MaLHP = lhp.MaLHP JOIN MonHoc mh ON lhp.MaMH = mh.MaMH " +
                "   LEFT JOIN QuyDinhHocPhi qd ON lhp.NamHoc = qd.NamHoc AND lhp.HocKy = qd.HocKy GROUP BY dk.MaSV " +
                ") TienHoc ON sv.MaSV = TienHoc.MaSV LEFT JOIN ( " +
                "   SELECT MaSV, SUM(SoTienThu) AS DaNop FROM PhieuThu GROUP BY MaSV " +
                ") TienNop ON sv.MaSV = TienNop.MaSV";
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) listTaiChinh.add(new BaoCaoTaiChinh(rs.getString("MaSV"), rs.getString("HoTen"), rs.getString("MaLop"), rs.getLong("TongPhatSinh"), rs.getLong("DaNop")));
        } catch (SQLException e) { }
    }

    @FXML private void handleXuatExcelTaiChinh() {
        exportToCSV(tableTaiChinh, "BaoCaoTaiChinh.csv", "Mã SV;Họ Tên;Lớp;Tổng Phát Sinh;Đã Nộp;Còn Nợ", item -> {
            String lop = item.getLop() == null || item.getLop().equals("null") ? "Chưa xếp lớp" : item.getLop();
            return String.format("%s;%s;%s;%d;%d;%d", item.getMaSV(), item.getHoTen(), lop, item.getTongPhatSinh(), item.getDaNop(), item.getConNo());
        });
    }

    // ==============================================================================
    // ============================ LOGIC TAB 2: ĐÀO TẠO ============================
    // ==============================================================================
    private void loadDuLieuBieuDoDaoTao() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        String sql = "SELECT SUM(CASE WHEN DiemSo >= 8.0 THEN 1 ELSE 0 END) as Gioi, " +
                "SUM(CASE WHEN DiemSo >= 6.5 AND DiemSo < 8.0 THEN 1 ELSE 0 END) as Kha, " +
                "SUM(CASE WHEN DiemSo >= 5.0 AND DiemSo < 6.5 THEN 1 ELSE 0 END) as TB, " +
                "SUM(CASE WHEN DiemSo > 0 AND DiemSo < 5.0 THEN 1 ELSE 0 END) as Yeu FROM DangKy WHERE DiemSo > 0";
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                if(rs.getInt("Gioi") > 0) data.add(new PieChart.Data("Giỏi", rs.getInt("Gioi")));
                if(rs.getInt("Kha") > 0) data.add(new PieChart.Data("Khá", rs.getInt("Kha")));
                if(rs.getInt("TB") > 0) data.add(new PieChart.Data("TB", rs.getInt("TB")));
                if(rs.getInt("Yeu") > 0) data.add(new PieChart.Data("Yếu/Trượt", rs.getInt("Yeu")));
            }
        } catch (SQLException e) { }
        chartHocLuc.setData(data);
    }

    private void setupTableDaoTao() {
        colMaSV_DT.setCellValueFactory(cell -> cell.getValue().maSVProperty());
        colHoTen_DT.setCellValueFactory(cell -> cell.getValue().hoTenProperty());
        colTenMH_DT.setCellValueFactory(cell -> cell.getValue().tenMHProperty());
        colMaLHP_DT.setCellValueFactory(cell -> cell.getValue().maLHPProperty());
        colDiem_DT.setCellValueFactory(cell -> cell.getValue().diemSoProperty());
        colXepLoai_DT.setCellValueFactory(cell -> cell.getValue().xepLoaiProperty());

        cbLocHocLucDT.getItems().addAll("Tất cả", "Giỏi", "Khá", "Trung bình", "Yếu/Trượt", "Chưa có điểm");
        cbLocHocLucDT.setValue("Tất cả");

        filteredDataDT = new FilteredList<>(listDaoTao, p -> true);
        txtTimKiemDT.textProperty().addListener((obs, old, newVal) -> filterDaoTao());
        cbLocHocLucDT.valueProperty().addListener((obs, old, newVal) -> filterDaoTao());

        SortedList<BaoCaoDaoTao> sortedData = new SortedList<>(filteredDataDT);
        sortedData.comparatorProperty().bind(tableDaoTao.comparatorProperty());
        tableDaoTao.setItems(sortedData);
    }

    private void filterDaoTao() {
        String keyword = txtTimKiemDT.getText().toLowerCase().trim();
        String loai = cbLocHocLucDT.getValue();
        filteredDataDT.setPredicate(bc -> {
            boolean matchKey = keyword.isEmpty() || bc.getMaSV().toLowerCase().contains(keyword) ||
                    bc.getHoTen().toLowerCase().contains(keyword) || bc.getTenMH().toLowerCase().contains(keyword);
            boolean matchLoai = loai.equals("Tất cả") || bc.getXepLoai().equals(loai);
            return matchKey && matchLoai;
        });
    }

    private void loadDuLieuBangDaoTao() {
        listDaoTao.clear();
        String sql = "SELECT sv.MaSV, sv.HoDem + ' ' + sv.Ten AS HoTen, mh.TenMH, dk.MaLHP, ISNULL(dk.DiemSo, 0) as DiemSo " +
                "FROM DangKy dk JOIN SinhVien sv ON dk.MaSV = sv.MaSV " +
                "JOIN LopHocPhan lhp ON dk.MaLHP = lhp.MaLHP JOIN MonHoc mh ON lhp.MaMH = mh.MaMH";
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) listDaoTao.add(new BaoCaoDaoTao(rs.getString("MaSV"), rs.getString("HoTen"), rs.getString("TenMH"), rs.getString("MaLHP"), rs.getDouble("DiemSo")));
        } catch (SQLException e) { }
    }

    @FXML private void handleXuatExcelDaoTao() {
        exportToCSV(tableDaoTao, "BaoCaoDaoTao.csv", "Mã SV;Họ Tên;Môn Học;Mã LHP;Điểm Số;Xếp Loại", item ->
                String.format("%s;%s;%s;%s;%.2f;%s", item.getMaSV(), item.getHoTen(), item.getTenMH(), item.getMaLHP(), item.getDiemSo(), item.getXepLoai())
        );
    }

    // ==============================================================================
    // ============================ LOGIC TAB 3: HỆ THỐNG ===========================
    // ==============================================================================
    private void loadDuLieuBieuDoHeThong() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sinh viên");
        String sql = "SELECT k.TenKhoa, COUNT(sv.MaSV) as SL FROM SinhVien sv " +
                "JOIN LopHanhChinh lhc ON sv.MaLop = lhc.MaLop JOIN ChuongTrinhDaoTao ctdt ON lhc.MaCTDT = ctdt.MaCTDT " +
                "JOIN Khoa k ON ctdt.MaKhoa = k.MaKhoa GROUP BY k.TenKhoa";
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) series.getData().add(new XYChart.Data<>(rs.getString("TenKhoa"), rs.getInt("SL")));
        } catch (SQLException e) { }
        chartMatDoSV.getData().add(series);
    }

    private void setupTableHeThong() {
        colMaSV_HT.setCellValueFactory(cell -> cell.getValue().maSVProperty());
        colHoTen_HT.setCellValueFactory(cell -> cell.getValue().hoTenProperty());
        colLop_HT.setCellValueFactory(cell -> cell.getValue().lopProperty());
        colKhoa_HT.setCellValueFactory(cell -> cell.getValue().khoaProperty());
        colTrangThai_HT.setCellValueFactory(cell -> cell.getValue().trangThaiProperty());

        cbLocKhoaHT.getItems().add("Tất cả Khoa");
        cbLocKhoaHT.setValue("Tất cả Khoa");
        // Load danh sách Khoa vào ComboBox
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT TenKhoa FROM Khoa"); ResultSet rs = ps.executeQuery()) {
            while(rs.next()) cbLocKhoaHT.getItems().add(rs.getString("TenKhoa"));
        } catch (SQLException e) {}

        filteredDataHT = new FilteredList<>(listHeThong, p -> true);
        txtTimKiemHT.textProperty().addListener((obs, old, newVal) -> filterHeThong());
        cbLocKhoaHT.valueProperty().addListener((obs, old, newVal) -> filterHeThong());

        SortedList<BaoCaoHeThong> sortedData = new SortedList<>(filteredDataHT);
        sortedData.comparatorProperty().bind(tableHeThong.comparatorProperty());
        tableHeThong.setItems(sortedData);
    }

    private void filterHeThong() {
        String keyword = txtTimKiemHT.getText().toLowerCase().trim();
        String khoa = cbLocKhoaHT.getValue();
        filteredDataHT.setPredicate(bc -> {
            boolean matchKey = keyword.isEmpty() || bc.getMaSV().toLowerCase().contains(keyword) || bc.getHoTen().toLowerCase().contains(keyword) || bc.getLop().toLowerCase().contains(keyword);
            boolean matchKhoa = khoa.equals("Tất cả Khoa") || bc.getKhoa().equals(khoa);
            return matchKey && matchKhoa;
        });
    }

    private void loadDuLieuBangHeThong() {
        listHeThong.clear();
        String sql = "SELECT sv.MaSV, sv.HoDem + ' ' + sv.Ten AS HoTen, sv.MaLop, sv.TrangThaiSV, k.TenKhoa " +
                "FROM SinhVien sv LEFT JOIN LopHanhChinh lhc ON sv.MaLop = lhc.MaLop " +
                "LEFT JOIN ChuongTrinhDaoTao ctdt ON lhc.MaCTDT = ctdt.MaCTDT LEFT JOIN Khoa k ON ctdt.MaKhoa = k.MaKhoa";
        try (Connection conn = KNDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String khoa = rs.getString("TenKhoa") != null ? rs.getString("TenKhoa") : "Chưa phân khoa";
                listHeThong.add(new BaoCaoHeThong(rs.getString("MaSV"), rs.getString("HoTen"), rs.getString("MaLop"), khoa, rs.getString("TrangThaiSV")));
            }
        } catch (SQLException e) { }
    }

    @FXML private void handleXuatExcelHeThong() {
        exportToCSV(tableHeThong, "BaoCaoHeThongSV.csv", "Mã SV;Họ Tên;Lớp;Khoa;Trạng Thái", item ->
                String.format("%s;%s;%s;%s;%s", item.getMaSV(), item.getHoTen(), item.getLop(), item.getKhoa(), item.getTrangThai())
        );
    }

    // ================== HÀM XUẤT EXCEL DÙNG CHUNG (TỐI ƯU CODE) ==================
    private <T> void exportToCSV(TableView<T> table, String fileName, String header, java.util.function.Function<T, String> rowMapper) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo Excel (CSV)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName(fileName);

        File file = fileChooser.showSaveDialog(table.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
                writer.write('\ufeff'); // BOM cho Tiếng Việt
                writer.println(header);
                for (T item : table.getItems()) writer.println(rowMapper.apply(item));

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đã xuất file " + fileName + " thành công!", ButtonType.OK);
                alert.showAndWait();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Lỗi khi xuất file: " + e.getMessage(), ButtonType.OK).showAndWait();
            }
        }
    }
}