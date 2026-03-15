package model.nhethong;

public class ThangDiem {
    private int idThangDiem;
    private double mucDiem;
    private String diemChu;
    private String ghiChu;

    public ThangDiem(int idThangDiem, double mucDiem, String diemChu, String ghiChu) {
        this.idThangDiem = idThangDiem;
        this.mucDiem = mucDiem;
        this.diemChu = diemChu;
        this.ghiChu = ghiChu;
    }

    public int getIdThangDiem() {
        return idThangDiem;
    }

    public void setIdThangDiem(int idThangDiem) {
        this.idThangDiem = idThangDiem;
    }

    public double getMucDiem() {
        return mucDiem;
    }

    public void setMucDiem(double mucDiem) {
        this.mucDiem = mucDiem;
    }

    public String getDiemChu() {
        return diemChu;
    }

    public void setDiemChu(String diemChu) {
        this.diemChu = diemChu;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

}
