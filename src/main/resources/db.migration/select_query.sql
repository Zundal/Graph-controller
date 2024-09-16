SELECT
    ug_ancestor.group_name AS ancestor_group_name,
    ug_descendant.group_name AS descendant_group_name,
    ugc.distance AS group_depth
FROM
    user_group_closure ugc
        JOIN
    user_group ug_ancestor ON ugc.ancestor_group_id = ug_ancestor.group_id
        JOIN
    user_group ug_descendant ON ugc.descendant_group_id = ug_descendant.group_id
ORDER BY
    ugc.ancestor_group_id, ugc.distance;

