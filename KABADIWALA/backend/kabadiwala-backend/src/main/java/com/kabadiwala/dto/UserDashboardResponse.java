package com.kabadiwala.dto;

public class UserDashboardResponse {
    private UserResponse user;
    private long unreadNotificationsCount;
    private String preferredLanguage;

    // Clean integration placeholders for future modules (Modules 2 & 3)
    private Object walletSummary;
    private Object pointsSummary;
    private Object upcomingPickup;
    private Object recentActivity;

    public UserDashboardResponse() {
    }

    public UserDashboardResponse(UserResponse user, long unreadNotificationsCount, String preferredLanguage) {
        this.user = user;
        this.unreadNotificationsCount = unreadNotificationsCount;
        this.preferredLanguage = preferredLanguage;
        this.walletSummary = null;
        this.pointsSummary = null;
        this.upcomingPickup = null;
        this.recentActivity = null;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public long getUnreadNotificationsCount() {
        return unreadNotificationsCount;
    }

    public void setUnreadNotificationsCount(long unreadNotificationsCount) {
        this.unreadNotificationsCount = unreadNotificationsCount;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public Object getWalletSummary() {
        return walletSummary;
    }

    public void setWalletSummary(Object walletSummary) {
        this.walletSummary = walletSummary;
    }

    public Object getPointsSummary() {
        return pointsSummary;
    }

    public void setPointsSummary(Object pointsSummary) {
        this.pointsSummary = pointsSummary;
    }

    public Object getUpcomingPickup() {
        return upcomingPickup;
    }

    public void setUpcomingPickup(Object upcomingPickup) {
        this.upcomingPickup = upcomingPickup;
    }

    public Object getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(Object recentActivity) {
        this.recentActivity = recentActivity;
    }
}
