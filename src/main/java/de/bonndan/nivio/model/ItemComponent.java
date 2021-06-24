package de.bonndan.nivio.model;

import org.springframework.lang.Nullable;

/**
 * Interface for items and item DTOs.
 *
 *
 */
public interface ItemComponent extends Component {

    @Nullable
    String getGroup();
}
