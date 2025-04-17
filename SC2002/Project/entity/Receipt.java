// SC2002/Project/entity/Receipt.java
package SC2002.Project.entity;

import SC2002.Project.entity.enums.FlatType;

public record Receipt(int receiptId,
                      String applicantName,
                      String nric,
                      FlatType flatType,
                      String projectName) {}
