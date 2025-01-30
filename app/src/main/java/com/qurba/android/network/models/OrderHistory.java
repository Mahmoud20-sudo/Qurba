package com.qurba.android.network.models;

import java.io.Serializable;

public class OrderHistory implements Serializable {
    private String createdAt;
    private String _id;
    private String status;
    private String cancellationReason;
    private String cancellationBecauseOf;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancellationBecauseOf() {
        return cancellationBecauseOf;
    }

    public void setCancellationBecauseOf(String cancellationBecauseOf) {
        this.cancellationBecauseOf = cancellationBecauseOf;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}