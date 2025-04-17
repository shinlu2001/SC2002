// SC2002/Project/util/IdGenerator.java
package SC2002.Project.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {
    private static final AtomicInteger PROJECT      = new AtomicInteger(1);
    private static final AtomicInteger APP          = new AtomicInteger(1);
    private static final AtomicInteger ENQ          = new AtomicInteger(1);
    private static final AtomicInteger FLAT         = new AtomicInteger(1);
    private static final AtomicInteger REGISTRATION = new AtomicInteger(1);
    private static final AtomicInteger RECEIPT      = new AtomicInteger(1); 
    public static int nextProjectId()       { return PROJECT.getAndIncrement(); }
    public static int nextApplicationId()   { return APP.getAndIncrement(); }
    public static int nextEnquiryId()       { return ENQ.getAndIncrement(); }
    public static int nextFlatId()          { return FLAT.getAndIncrement(); }
    public static int nextRegistrationId(String string)  { return REGISTRATION.getAndIncrement(); }
    public static int nextReceiptId()       { return RECEIPT.getAndIncrement(); }
}
