package model.quanly;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class BaoCaoDaoTao {
    private final SimpleStringProperty maSV, hoTen, tenMH, maLHP, xepLoai;
    private final SimpleDoubleProperty diemSo;

    public BaoCaoDaoTao(String maSV, String hoTen, String tenMH, String maLHP, double diemSo) {
        this.maSV = new SimpleStringProperty(maSV);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.tenMH = new SimpleStringProperty(tenMH);
        this.maLHP = new SimpleStringProperty(maLHP);
        this.diemSo = new SimpleDoubleProperty(diemSo);

        // Tự động phân loại học lực dựa trên điểm
        String loai = "Chưa có điểm";
        if (diemSo > 0) {
            if (diemSo >= 8.0) loai = "Giỏi";
            else if (diemSo >= 6.5) loai = "Khá";
            else if (diemSo >= 5.0) loai = "Trung bình";
            else if (diemSo >= 4) loai = "yếu";

            else loai = "Trượt";
        }
        this.xepLoai = new SimpleStringProperty(loai);
    }

    public String getMaSV() { return maSV.get(); }
    public String getHoTen() { return hoTen.get(); }
    public String getTenMH() { return tenMH.get(); }
    public String getMaLHP() { return maLHP.get(); }
    public double getDiemSo() { return diemSo.get(); }
    public String getXepLoai() { return xepLoai.get(); }

    public SimpleStringProperty maSVProperty() { return maSV; }
    public SimpleStringProperty hoTenProperty() { return hoTen; }
    public SimpleStringProperty tenMHProperty() { return tenMH; }
    public SimpleStringProperty maLHPProperty() { return maLHP; }
    public SimpleDoubleProperty diemSoProperty() { return diemSo; }
    public SimpleStringProperty xepLoaiProperty() { return xepLoai; }
}