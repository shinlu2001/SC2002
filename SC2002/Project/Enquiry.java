// package SC2002.Project;
class Enquiry {
    private String content;
    private String response="";
    private Project project=null;
    private User createdBy;
    private User repliedBy=null;
    public Enquiry(User user, String content) {
        createdBy = user;
        this.content = content;
    }
    public void setEnquiry(String r) {
        content = r;
    }
    public void setResponse(String r) {
        response = r;
    }
    public String getResponse() {
        return response;
    }
    public String getEnquiry() {
        return content;
    }
    public void setStaffReply(User staff) {
        repliedBy = staff;
    }
    public User getStaff() {
        return repliedBy;
    }
    public User getCreatedByUser() {
        return createdBy;
    }
    public void setProject(Project p) {
        project = p;
    }
    public Project getProject() {
        return project;
    }
}

