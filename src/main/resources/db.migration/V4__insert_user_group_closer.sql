-- 유저 그룹 간의 상하위 관계 데이터 삽입 (ancestor_group_id, descendant_group_id, distance)
INSERT INTO user_group_closure (ancestor_group_id, descendant_group_id, distance)
VALUES
    -- Root Group -> Group Level 1 (수평적 추가)
    (40, 41, 1), -- Root -> Group Level 1A
    (40, 42, 1), -- Root -> Group Level 1B
    (40, 43, 1), -- Root -> Group Level 1C
    -- Group Level 1 -> Group Level 2
    (41, 44, 1), -- Group Level 1A -> Group Level 2A
    (42, 45, 1), -- Group Level 1B -> Group Level 2B
    (43, 46, 1), -- Group Level 1C -> Group Level 2C
    -- Group Level 2 -> Group Level 3
    (44, 47, 1), -- Group Level 2A -> Group Level 3A
    (45, 48, 1), -- Group Level 2B -> Group Level 3B
    (46, 49, 1), -- Group Level 2C -> Group Level 3C
    -- Group Level 3 -> Group Level 4
    (47, 50, 1), -- Group Level 3A -> Group Level 4A
    (48, 51, 1), -- Group Level 3B -> Group Level 4B
    (49, 52, 1), -- Group Level 3C -> Group Level 4C
    -- Group Level 4 -> Group Level 5
    (50, 53, 1), -- Group Level 4A -> Group Level 5A
    (51, 54, 1), -- Group Level 4B -> Group Level 5B
    (52, 55, 1), -- Group Level 4C -> Group Level 5C
    -- Group Level 5 -> Group Level 6
    (53, 56, 1), -- Group Level 5A -> Group Level 6A
    (54, 57, 1), -- Group Level 5B -> Group Level 6B
    (55, 58, 1), -- Group Level 5C -> Group Level 6C
    -- Group Level 6 -> Group Level 7
    (56, 59, 1), -- Group Level 6A -> Group Level 7A
    (57, 60, 1), -- Group Level 6B -> Group Level 7B
    (58, 61, 1), -- Group Level 6C -> Group Level 7C
    -- Group Level 7 -> Group Level 8
    (59, 62, 1), -- Group Level 7A -> Group Level 8A
    (60, 63, 1), -- Group Level 7B -> Group Level 8B
    (61, 64, 1), -- Group Level 7C -> Group Level 8C
    -- Group Level 8 -> Group Level 9
    (62, 65, 1), -- Group Level 8A -> Group Level 9A
    (63, 66, 1), -- Group Level 8B -> Group Level 9B
    (64, 67, 1), -- Group Level 8C -> Group Level 9C
    -- Group Level 9 -> Group Level 10
    (65, 68, 1), -- Group Level 9A -> Group Level 10A
    (66, 69, 1), -- Group Level 9B -> Group Level 10B
    (67, 70, 1); -- Group Level 9C -> Group Level 10C
