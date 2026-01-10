package at.fhtw.swen1.mrp.business;

import java.util.UUID;

public record UserRatingCount(UUID userId, int count) {
}
