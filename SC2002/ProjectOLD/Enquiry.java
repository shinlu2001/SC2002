package SC2002.ProjectOLD;
public class Enquiry {
    protected static int nextId = -1;
    private String content;
    private String response="";
    private Project project=null;
    private User createdBy;
    private User repliedBy=null;
    private String flatType=null;
    private int enId;
    public Enquiry(User user, String content) {
        createdBy = user;
        this.content = content;
        enId = ++nextId;
    }
    public void setEnquiry(String r) {
        content = r;
    }
    public String getflatType() {
        return flatType;
    }
    public int getId() {
        return enId;
    }
    public void setflatType(String r) {
        flatType = r;
    }
    public String getResponse() {
        return response;
    }
    public String getEnquiry() {
        return content;
    }
    public void setResponse(String r) {
        response = r;
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
    public void display() {
        // System.out.println("ID: " + enId);
        System.out.println("Enquiry: "+ content);
        System.out.println("Project: "+ (project!=null?project.getProjectName():null));
        System.out.println("Flat Type: "+flatType);
        if (repliedBy==null) {
            System.out.println("No reply to this enquiry yet.");
        } else {
            System.out.println("Response: "+response);
            System.out.println("Replied by: " + repliedBy.get_firstname());
        }
    }
}

