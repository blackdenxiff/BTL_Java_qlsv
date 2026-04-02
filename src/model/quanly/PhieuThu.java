package model.quanly;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class PhieuThu {
    private final SimpleStringProperty ngayThu;
    private final SimpleLongProperty soTien;
    private final SimpleStringProperty nguoiThu;
    private String soTienFormatted; // Dùng để hiển thị lên bảng kèm chữ "đ"

    // Constructor nhận giá trị thô từ Database
    public PhieuThu(String ngayThu, long soTien, String nguoiThu, String soTienFormatted) {
        this.ngayThu = new SimpleStringProperty(ngayThu);
        this.soTien = new SimpleLongProperty(soTien);
        this.nguoiThu = new SimpleStringProperty(nguoiThu);
        this.soTienFormatted = soTienFormatted;
    }

    // Các phương thức Property (Bắt buộc phải có để TableView hoạt động)
    public SimpleStringProperty ngayThuProperty() { return ngayThu; }
    public SimpleLongProperty soTienProperty() { return soTien; }
    public SimpleStringProperty nguoiThuProperty() { return nguoiThu; }

    // Thuộc tính bổ sung để hiện thị chuỗi đã format (ví dụ: "500.000 đ")
    public SimpleStringProperty soTienStringProperty() {
        return new SimpleStringProperty(soTienFormatted);
    }

    // Getters tiêu chuẩn
    public String getNgayThu() { return ngayThu.get(); }
    public long getSoTien() { return soTien.get(); }
    public String getNguoiThu() { return nguoiThu.get(); }
}