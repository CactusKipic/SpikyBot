package fr.cactus_industries.tools.permissionslevels;

public enum SBPermissionType {
    
    
    // Name of the permission type (Table of the stored permission level, Table of the stored required level)
    TicketChannel("ticketsrank", "ticketschannel"),
    PDFReading("pdfreadingrank", "t_pdfreading_channel");
    
    private final String permissionLevel;
    private final String requiredLevel;
    
    SBPermissionType(String permissionLevel, String requiredLevel){
        this.permissionLevel = permissionLevel;
        this.requiredLevel = requiredLevel;
    }
    
    public String getTablePermissionLevel() {
        return permissionLevel;
    }
    
    public String getTableRequiredLevel() {
        return requiredLevel;
    }
}
