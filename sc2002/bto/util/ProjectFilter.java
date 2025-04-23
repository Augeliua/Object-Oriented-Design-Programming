package sc2002.bto.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sc2002.bto.entity.Project;
import sc2002.bto.enums.FlatType;

/**
 * Utility class to filter and sort projects based on user preferences.
 * This class maintains the filter state during user navigation between menus.
 * It provides methods to filter projects by neighborhood and flat type,
 * as well as sorting projects by different criteria.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2024-04-23
 */
public class ProjectFilter {
    private String neighborhood;
    private FlatType flatType;
    private String sortBy; // Can be "name", "neighborhood", "openDate", etc.
    private boolean ascending = true;
    
    /**
     * Constructor with default values
     */
    public ProjectFilter() {
        // Default sort by name (alphabetical order)
        this.sortBy = "name";
    }
    
    /**
     * Constructor with specified parameters
     * 
     * @param neighborhood Filter by neighborhood
     * @param flatType Filter by flat type
     * @param sortBy Sort by field
     * @param ascending Sort order (true for ascending, false for descending)
     */
    public ProjectFilter(String neighborhood, FlatType flatType, String sortBy, boolean ascending) {
        this.neighborhood = neighborhood;
        this.flatType = flatType;
        this.sortBy = sortBy;
        this.ascending = ascending;
    }
    
    /**
     * Apply filters and sorting to a list of projects.
     * This method filters the list of projects based on the current filter settings:
     * - By neighborhood if a neighborhood filter is set
     * - By flat type if a flat type filter is set
     * Then it sorts the filtered list based on the sortBy field and sort order.
     * 
     * @param projects List of projects to filter and sort
     * @return A new list containing the filtered and sorted projects
     */
    public List<Project> apply(List<Project> projects) {
        List<Project> result = new ArrayList<>(projects);
        
        // Apply neighborhood filter
        if (neighborhood != null && !neighborhood.isEmpty()) {
            result.removeIf(p -> !p.getNeighborhood().equalsIgnoreCase(neighborhood));
        }
        
        // Apply flat type filter
        if (flatType != null) {
            result.removeIf(p -> {
                boolean hasSelectedFlatType = false;
                for (FlatType type : p.getFlatType()) {
                    if (type == flatType) {
                        hasSelectedFlatType = true;
                        break;
                    }
                }
                return !hasSelectedFlatType;
            });
        }
        
        // Apply sorting
        if (sortBy != null) {
            Comparator<Project> comparator = getComparator(sortBy);
            if (!ascending) {
                comparator = comparator.reversed();
            }
            Collections.sort(result, comparator);
        }
        
        return result;
    }
    
    /**
     * Get a comparator for sorting projects by the specified field.
     * This method returns a Comparator that can be used to sort projects by:
     * - name: Project name (alphabetical)
     * - neighborhood: Neighborhood name (alphabetical)
     * - opendate: Application opening date
     * - closedate: Application closing date
     * - tworoom: Number of 2-room units available
     * - threeroom: Number of 3-room units available
     * 
     * @param sortField The field to sort by (case-insensitive)
     * @return A Comparator for the specified field, defaults to name if field is invalid
     */
    private Comparator<Project> getComparator(String sortField) {
        switch (sortField.toLowerCase()) {
            case "name":
                return Comparator.comparing(Project::getProjectName);
            case "neighborhood":
                return Comparator.comparing(Project::getNeighborhood);
            case "opendate":
                return Comparator.comparing(Project::getApplicationOpenDate);
            case "closedate":
                return Comparator.comparing(Project::getApplicationCloseDate);
            case "tworoom":
                return Comparator.comparing(Project::getTwoRoomUnitsAvailable);
            case "threeroom":
                return Comparator.comparing(Project::getThreeRoomUnitsAvailable);
            default:
                return Comparator.comparing(Project::getProjectName); // Default to name
        }
    }

    // Getters and Setters
    /**
     * Gets the current neighborhood filter.
     * 
     * @return The neighborhood name being used as a filter, or null if not filtering by neighborhood
     */
    public String getNeighborhood() {
        return neighborhood;
    }
    
    /**
     * Sets the neighborhood filter.
     * 
     * @param neighborhood The neighborhood name to filter by, or null to clear this filter
     */
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
    
    /**
     * Gets the current flat type filter.
     * 
     * @return The flat type being used as a filter, or null if not filtering by flat type
     */
    public FlatType getFlatType() {
        return flatType;
    }
    
    /**
     * Sets the flat type filter.
     * 
     * @param flatType The flat type to filter by, or null to clear this filter
     */
    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }
    
    /**
     * Gets the current sort field.
     * 
     * @return The name of the field used for sorting projects
     */
    public String getSortBy() {
        return sortBy;
    }
    
    /**
     * Sets the field to sort projects by.
     * 
     * @param sortBy The name of the field to sort by ("name", "neighborhood", "opendate", etc.)
     */
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    /**
     * Checks if the current sort order is ascending.
     * 
     * @return true if sorting in ascending order, false if sorting in descending order
     */
    public boolean isAscending() {
        return ascending;
    }
    
    /**
     * Sets the sort order.
     * 
     * @param ascending true for ascending order, false for descending order
     */
    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
    
    /**
     * Resets all filters to default values.
     * Neighborhood and flat type filters are cleared (set to null).
     * Sort field is set to "name" (project name).
     * Sort order is set to ascending.
     */
    public void reset() {
        this.neighborhood = null;
        this.flatType = null;
        this.sortBy = "name";
        this.ascending = true;
    }
    
    /**
     * Returns a string representation of the current filter settings.
     * Includes neighborhood filter, flat type filter, sort field, and sort order
     * if they are set. Useful for displaying the current filter state to users.
     * 
     * @return A formatted string showing the current filter settings
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Filters: ");
        if (neighborhood != null) {
            sb.append("Neighborhood=").append(neighborhood).append(", ");
        }
        if (flatType != null) {
            sb.append("FlatType=").append(flatType).append(", ");
        }
        sb.append("SortBy=").append(sortBy).append(" (").append(ascending ? "Ascending" : "Descending").append(")");
        return sb.toString();
    }
}
