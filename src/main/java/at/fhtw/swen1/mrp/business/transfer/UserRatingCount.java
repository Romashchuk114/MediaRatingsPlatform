package at.fhtw.swen1.mrp.business.transfer;

import java.util.UUID;

public record UserRatingCount(UUID userId, int count) {
}
