package model.quanly;

import javafx.beans.property.SimpleStringProperty;

public class BaoCaoHeThong {
    private final SimpleStringProperty maSV, hoTen, lop, khoa, trangThai;

    public BaoCaoHeThong(String maSV, String hoTen, String lop, String khoa, String trangThai) {
        this.maSV = new SimpleStringProperty(maSV);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.lop = new SimpleStringProperty(lop);
        this.khoa = new SimpleStringProperty(khoa);
        this.trangThai = new SimpleStringProperty(trangThai != null ? trangThai : "Chưa cập nhật");
    }

    public String getMaSV() { return maSV.get(); }
    public String getHoTen() { return hoTen.get(); }
    public String getLop() { return lop.get(); }
    public String getKhoa() { return khoa.get(); }
    public String getTrangThai() { return trangThai.get(); }

    public SimpleStringProperty maSVProperty() { return maSV; }
    public SimpleStringProperty hoTenProperty() { return hoTen; }
    public SimpleStringProperty lopProperty() { return lop; }
    public SimpleStringProperty khoaProperty() { return khoa; }
    public SimpleStringProperty trangThaiProperty() { return trangThai; }
}