package controller.nhanvien;

import dao.nconnguoidao.SinhVienDAO;
import model.nconnguoi.SinhVien;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UpdateSinhVien {

    private SinhVienDAO svDAO = new SinhVienDAO();

    // Dùng d/M/yyyy để Java đọc được cả ngày có số 0 (05/03/2000) và không có số 0 (5/3/2000)
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
    // Dùng cái này để xuất ra cho đẹp (luôn có số 0)
    private final DateTimeFormatter exportFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField txtMaSV;
    @FXML private TextField txtHoDem;
    @FXML private TextField txtTen;
    @FXML private TextField txtGioiTinh;
    @FXML private TextField txtTenKhoa;
    @FXML private DatePicker dpNgaySinh;

    @FXML private TableView<SinhVien> tableSinhVien;
    @FXML private TableColumn<SinhVien, String> colMaSV;
    @FXML private TableColumn<SinhVien, String> colHoTen;
    @FXML private TableColumn<SinhVien, LocalDate> colNgaySinh;
    @FXML private TableColumn<SinhVien, String> colGioiTinh;
    @FXML private TableColumn<SinhVien, String> colLop;
    @FXML private TableColumn<SinhVien, String> colTrangThai;

    @FXML
    public void initialize() {
        colMaSV.setCellValueFactory(new PropertyValueFactory<>("maSV"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoDem"));
        colNgaySinh.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        colGioiTinh.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        colLop.setCellValueFactory(new PropertyValueFactory<>("maLop"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThaiSV"));

        loadDataToTable();
    }

    private void loadDataToTable() {
        List<SinhVien> list = svDAO.getAllSinhVien();
        ObservableList<SinhVien> observableList = FXCollections.observableArrayList(list);
        tableSinhVien.setItems(observableList);
    }

    @FXML
    public void handleTimKiem() {
        String maSV = txtMaSV.getText().trim();
        if (maSV.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Mã SV cần tìm!");
            return;
        }

        SinhVien sv = svDAO.getSinhVienByMa(maSV);
        if (sv != null) {
            txtHoDem.setText(sv.getHoDem());
            txtTen.setText(sv.getTen());
            if (sv.getNgaySinh() != null) dpNgaySinh.setValue(sv.getNgaySinh());
            txtGioiTinh.setText(sv.getGioiTinh());

            ObservableList<SinhVien> list = FXCollections.observableArrayList();
            list.add(sv);
            tableSinhVien.setItems(list);
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy sinh viên!");
        }
    }

    @FXML
    public void handleThem() {
        SinhVien sv = layDuLieuTuForm();
        svDAO.insertSinhVien(sv);
        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm sinh viên mới!");
        loadDataToTable();
    }

    @FXML
    public void handleSua() {
        SinhVien sv = layDuLieuTuForm();
        svDAO.updateSinhVien(sv);
        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin sinh viên!");
        loadDataToTable();
    }

    @FXML
    public void handleTimKiemTheoMaKhoa() {
        String maKhoa = txtTenKhoa.getText().trim();
        if(maKhoa.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập Mã Khoa vào ô Tên Khoa!");
            return;
        }

        List<SinhVien> list = svDAO.findByMaKhoa(maKhoa);
        tableSinhVien.setItems(FXCollections.observableArrayList(list));

        if(list.isEmpty()){
            showAlert(Alert.AlertType.INFORMATION, "Kết quả", "Không có sinh viên nào thuộc khoa này!");
        }
    }

    @FXML
    public void handleTimKiemNangCao() {
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Vui lòng thiết kế thêm giao diện nhập năm nhập học để dùng tính năng này!");
    }

    // ================= CHỨC NĂNG IMPORT =================
    @FXML
    public void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file dữ liệu (CSV)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                int count = 0;
                boolean isFirstLine = true;

                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    String[] data = line.split(",");
                    if (data.length >= 7) {
                        SinhVien sv = new SinhVien();

                        sv.setMaSV(data[0].trim());
                        sv.setHoDem(data[1].trim());
                        sv.setTen(data[2].trim());

                        // Xử lý ngày sinh an toàn tuyệt đối
                        String ngaySinhStr = data[3].trim();
                        if (!ngaySinhStr.isEmpty() && !ngaySinhStr.equalsIgnoreCase("null")) {
                            // Dùng dateFormatter (d/M/yyyy) để đọc mọi trường hợp
                            sv.setNgaySinh(LocalDate.parse(ngaySinhStr, dateFormatter));
                        }

                        sv.setGioiTinh(data[4].trim());
                        sv.setMaLop(data[5].trim());
                        sv.setTrangThaiSV(data[6].trim());

                        if (svDAO.getSinhVienByMa(sv.getMaSV()) != null) {
                            svDAO.updateSinhVien(sv);
                        } else {
                            svDAO.insertSinhVien(sv);
                        }
                        count++;
                    }
                }
                loadDataToTable();
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã Import thành công " + count + " sinh viên vào CSDL!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Import", "Cấu trúc file không hợp lệ hoặc sai định dạng ngày tháng!\n" + e.getMessage());
            }
        }
    }

    // ================= CHỨC NĂNG EXPORT =================
    @FXML
    public void handleExport() {
        ObservableList<SinhVien> danhSach = tableSinhVien.getItems();
        if (danhSach == null || danhSach.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Bảng dữ liệu đang trống, không có gì để xuất!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file dữ liệu (CSV)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("DanhSachSinhVien.csv");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                bw.write('\ufeff');
                bw.write("MaSV,HoDem,Ten,NgaySinh,GioiTinh,Lop,TrangThai\n");

                for (SinhVien sv : danhSach) {
                    // THỦ THUẬT: Thêm \t vào trước Mã SV và Ngày Sinh để đánh lừa Excel
                    String maSV_Format = "\t" + sv.getMaSV();
                    String ngaySinhFormat = (sv.getNgaySinh() != null) ? "\t" + sv.getNgaySinh().format(exportFormatter) : "";

                    String line = String.format("%s,%s,%s,%s,%s,%s,%s\n",
                            maSV_Format, sv.getHoDem(), sv.getTen(),
                            ngaySinhFormat, sv.getGioiTinh(), sv.getMaLop(), sv.getTrangThaiSV());
                    bw.write(line);
                }

                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xuất dữ liệu ra file thành công!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Export", "Có lỗi xảy ra khi lưu file: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleClickTable(MouseEvent event) {
        SinhVien svSelected = tableSinhVien.getSelectionModel().getSelectedItem();
        if (svSelected != null) {
            txtMaSV.setText(svSelected.getMaSV());
            txtHoDem.setText(svSelected.getHoDem());
            txtTen.setText(svSelected.getTen());
            txtGioiTinh.setText(svSelected.getGioiTinh());
            if (svSelected.getNgaySinh() != null) {
                dpNgaySinh.setValue(svSelected.getNgaySinh());
            }
        }
    }

    private SinhVien layDuLieuTuForm() {
        SinhVien sv = new SinhVien();
        sv.setMaSV(txtMaSV.getText().trim());
        sv.setHoDem(txtHoDem.getText().trim());
        sv.setTen(txtTen.getText().trim());
        sv.setNgaySinh(dpNgaySinh.getValue());
        sv.setGioiTinh(txtGioiTinh.getText().trim());
        sv.setMaLop("");
        sv.setSdt("");
        sv.setEmail("");
        sv.setTrangThaiSV("Đang học");
        return sv;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}