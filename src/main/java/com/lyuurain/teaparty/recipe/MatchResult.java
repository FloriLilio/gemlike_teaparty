package com.lyuurain.teaparty.recipe;

public record MatchResult(int totalTicks, float totalYield) {
    public static final MatchResult EMPTY = new MatchResult(0, 0.0F);
}
