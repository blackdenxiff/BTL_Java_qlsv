package model.quanly;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import java.text.NumberFormat;
import java.util.Locale;

public class BaoCaoTaiChinh {
    private final SimpleStringProperty maSV;
    private final SimpleStringProperty hoTen;
    private final SimpleStringProperty lop;
    private final SimpleLongProperty tongPhatSinh;
    private final SimpleLongProperty daNop;
    private final SimpleLongProperty conNo;

    private NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    public BaoCaoTaiChinh(String maSV, String hoTen, String lop, long tongPhatSinh, long daNop) {
        this.maSV = new SimpleStringProperty(maSV);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.lop = new SimpleStringProperty(lop);
        this.tongPhatSinh = new SimpleLongProperty(tongPhatSinh);
        this.daNop = new SimpleLongProperty(daNop);
        this.conNo = new SimpleLongProperty(tongPhatSinh - daNop);
    }

    // --- Các hàm Getter chuẩn để lấy dữ liệu (dùng khi xuất Excel) ---
    public String getMaSV() { return maSV.get(); }
    public String getHoTen() { return hoTen.get(); }
    public String getLop() { return lop.get(); }
    public long getTongPhatSinh() { return tongPhatSinh.get(); }
    public long getDaNop() { return daNop.get(); }
    public long getConNo() { return conNo.get(); }

    // --- Các hàm Property (Bắt buộc phải có để TableView hiển thị được) ---
    public SimpleStringProperty maSVProperty() { return maSV; }
    public SimpleStringProperty hoTenProperty() { return hoTen; }
    public SimpleStringProperty lopProperty() { return lop; }

    // Format tiền tệ trực tiếp trong Model để TableView hiện đẹp (vd: "500.000")
    public SimpleStringProperty tongPhatSinhStrProperty() {
        return new SimpleStringProperty(currencyFormat.format(tongPhatSinh.get()));
    }
    public SimpleStringProperty daNopStrProperty() {
        return new SimpleStringProperty(currencyFormat.format(daNop.get()));
    }
    public SimpleStringProperty conNoStrProperty() {
        return new SimpleStringProperty(currencyFormat.format(conNo.get()));
    }
}