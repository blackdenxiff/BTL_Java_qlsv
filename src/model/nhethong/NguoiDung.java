package model.nhethong;

public class NguoiDung {
    private String username;
    private String matKhau;
    private String usertype;
    private String maSV; // Khóa ngoại (có thể null)
    private String maNV;

    public NguoiDung(){

    }
    public NguoiDung(String username, String matKhau, String usertype, String maSV, String maNV) {
        this.username = username;
        this.matKhau = matKhau;
        this.usertype = usertype;
        this.maSV = maSV;
        this.maNV = maNV;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

}
