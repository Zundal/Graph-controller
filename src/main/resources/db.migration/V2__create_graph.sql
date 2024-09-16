-- 유저 테이블 (기본 정보를 저장)
-- 각 유저의 고유 ID와 이름을 저장
CREATE TABLE User
(
    user_id  INT PRIMARY KEY AUTO_INCREMENT COMMENT '유저의 고유 식별자 (자동 증가)', -- 유저의 고유 ID에 대한 주석
    username VARCHAR(255) NOT NULL COMMENT '유저 이름'                        -- 유저 이름에 대한 주석
) COMMENT = '유저 정보를 저장하는 테이블';

-- 유저 그룹 테이블 (그룹 정보를 저장)
-- 각 그룹의 고유 ID와 이름을 저장
CREATE TABLE UserGroup
(
    group_id   INT PRIMARY KEY AUTO_INCREMENT COMMENT '유저 그룹의 고유 식별자 (자동 증가)', -- 그룹의 고유 ID에 대한 주석
    group_name VARCHAR(255) NOT NULL COMMENT '유저 그룹 이름'                        -- 그룹 이름에 대한 주석
) COMMENT = '유저 그룹 정보를 저장하는 테이블';

-- Closure Table (유저 그룹 간의 계층 구조를 저장)
-- 각 그룹의 상위-하위 관계를 트리 구조로 저장
CREATE TABLE UserGroupClosure
(
    ancestor_group_id   INT COMMENT '상위 그룹의 ID (조상 그룹)',                                                                -- 상위 그룹 ID에 대한 주석
    descendant_group_id INT COMMENT '하위 그룹의 ID (자손 그룹)',                                                                -- 하위 그룹 ID에 대한 주석
    distance            INT COMMENT '상위 그룹과 하위 그룹 간의 거리 (0은 자기 자신)',                                                    -- 거리(distance)에 대한 설명
    PRIMARY KEY (ancestor_group_id, descendant_group_id),                                                               -- 상위-하위 그룹 간 관계를 고유하게 정의
    FOREIGN KEY (ancestor_group_id) REFERENCES UserGroup (group_id) ON DELETE CASCADE,  -- 상위 그룹 삭제 시의 동작
    FOREIGN KEY (descendant_group_id) REFERENCES UserGroup (group_id) ON DELETE CASCADE  -- 하위 그룹 삭제 시의 동작
) COMMENT = '유저 그룹 간의 계층 구조를 저장하는 테이블';

-- 인덱스 생성 (계층 구조 조회 성능 최적화를 위해)
CREATE INDEX idx_closure_ancestor ON UserGroupClosure (ancestor_group_id) COMMENT '상위 그룹 ID를 기준으로 빠른 검색을 위한 인덱스';
CREATE INDEX idx_closure_descendant ON UserGroupClosure (descendant_group_id) COMMENT '하위 그룹 ID를 기준으로 빠른 검색을 위한 인덱스';
