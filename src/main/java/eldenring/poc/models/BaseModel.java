package eldenring.poc.models;

import java.io.Serializable;

/**
 * Abstract base model for all catalog items.
 * Provides common fields and methods for items displayed in the catalog.
 */
public abstract class BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Gets the display title/name of the item.
     *
     * @return The title/name of the item
     */
    public abstract String getDisplayName();

    /**
     * Gets the image URL of the item.
     *
     * @return The image URL
     */
    public abstract String getImageUrl();
}

