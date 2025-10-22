package eldenring.poc.dto;

import eldenring.poc.models.Ammo;

import java.util.List;

public class AmmoResponse {
    private boolean success;
    private int count;
    private int total;
    private List<Ammo> data;

    public AmmoResponse() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setTotal(int total) { this.total = total; }

    public int getTotal() { return total; }

    public List<Ammo> getData() {
        return data;
    }

    public void setData(List<Ammo> data) {
        this.data = data;
    }
}

