-- Stored procedure để tính toán gợi ý bạn bè dựa trên bạn chung
DROP PROCEDURE IF EXISTS calculate_friend_suggestions;

CREATE PROCEDURE calculate_friend_suggestions(IN user_id INT)
BEGIN
    -- Xóa gợi ý cũ
    DELETE FROM lich_su_goi_y WHERE id_nguoi_duoc_goi_y = user_id;
    
    -- Tạo bảng tạm để lưu kết quả
    DROP TEMPORARY TABLE IF EXISTS temp_friend_scores;
    CREATE TEMPORARY TABLE temp_friend_scores AS
    SELECT DISTINCT
        user_id as id_nguoi_duoc_goi_y,
        n2.id as id_nguoi_trong_goi_y,
        dt.id as id_diem_tuong_tac,
        JSON_OBJECT(
            'ban_chung', COUNT(DISTINCT k1.id_nguoi_nhan),
            'so_thich_chung', IFNULL(
                (SELECT COUNT(*) 
                FROM thuoc_tinh_nguoi_dung t1, thuoc_tinh_nguoi_dung t2 
                WHERE t1.id_nguoi_dung = user_id 
                AND t2.id_nguoi_dung = n2.id 
                AND JSON_OVERLAPS(t1.so_thich, t2.so_thich)
                ), 0),
            'dia_diem', CASE 
                WHEN MAX(t1.tinh_thanh) = MAX(t2.tinh_thanh) THEN 10
                ELSE 0
            END
        ) as ly_do_goi_y,
        (COUNT(DISTINCT k1.id_nguoi_nhan) * 10) + 
        IFNULL((SELECT COUNT(*) 
                FROM thuoc_tinh_nguoi_dung t1, thuoc_tinh_nguoi_dung t2 
                WHERE t1.id_nguoi_dung = user_id 
                AND t2.id_nguoi_dung = n2.id 
                AND JSON_OVERLAPS(t1.so_thich, t2.so_thich)
                ), 0) * 5 +
        CASE WHEN MAX(t1.tinh_thanh) = MAX(t2.tinh_thanh) THEN 10 ELSE 0 END as diem_goi_y,
        'hybrid' as nguon_goi_y
    FROM nguoi_dung n1
    JOIN ket_ban k1 ON n1.id = k1.id_nguoi_gui AND k1.trang_thai = 'ban_be'
    JOIN ket_ban k2 ON k1.id_nguoi_nhan = k2.id_nguoi_gui AND k2.trang_thai = 'ban_be'
    JOIN nguoi_dung n2 ON k2.id_nguoi_nhan = n2.id
    LEFT JOIN diem_tuong_tac dt ON (dt.id_nguoi_1 = user_id AND dt.id_nguoi_2 = n2.id)
    LEFT JOIN thuoc_tinh_nguoi_dung t1 ON n1.id = t1.id_nguoi_dung
    LEFT JOIN thuoc_tinh_nguoi_dung t2 ON n2.id = t2.id_nguoi_dung
    WHERE n1.id = user_id
    AND n2.id != user_id
    AND n2.id NOT IN (
        SELECT id_nguoi_nhan FROM ket_ban WHERE id_nguoi_gui = user_id
        UNION
        SELECT id_nguoi_gui FROM ket_ban WHERE id_nguoi_nhan = user_id
    )
    GROUP BY n2.id, dt.id
    HAVING COUNT(DISTINCT k1.id_nguoi_nhan) > 0;

    -- Chèn vào bảng chính từ bảng tạm
    INSERT INTO lich_su_goi_y (
        id_nguoi_duoc_goi_y,
        id_nguoi_trong_goi_y,
        id_diem_tuong_tac,
        ly_do_goi_y,
        diem_goi_y,
        nguon_goi_y
    )
    SELECT 
        id_nguoi_duoc_goi_y,
        id_nguoi_trong_goi_y,
        id_diem_tuong_tac,
        ly_do_goi_y,
        diem_goi_y,
        nguon_goi_y
    FROM temp_friend_scores
    ORDER BY diem_goi_y DESC
    LIMIT 50;

    -- Xóa bảng tạm
    DROP TEMPORARY TABLE IF EXISTS temp_friend_scores;
END;

-- Stored procedure để phân tích hành vi người dùng hàng ngày
DROP PROCEDURE IF EXISTS analyze_user_behavior;
DELIMITER //
CREATE PROCEDURE analyze_user_behavior(IN user_id INT)
BEGIN
    DECLARE today DATE;
    SET today = CURDATE();
    
    -- Kiểm tra xem đã có bản ghi cho ngày hôm nay chưa
    IF NOT EXISTS (SELECT 1 FROM phan_tich_hanh_vi WHERE id_nguoi_dung = user_id AND ngay_phan_tich = today) THEN
        -- Tạo bản ghi mới
        INSERT INTO phan_tich_hanh_vi (
            id_nguoi_dung, 
            ngay_phan_tich, 
            so_bai_viet_dang, 
            so_binh_luan, 
            so_like_gui, 
            so_like_nhan,
            so_profile_xem,
            so_tin_nhan_gui,
            thoi_gian_online
        )
        SELECT 
            user_id,
            today,
            (SELECT COUNT(*) FROM bai_viet WHERE id_nguoi_dung = user_id AND DATE(ngay_tao) = today),
            (SELECT COUNT(*) FROM binh_luan WHERE id_nguoi_dung = user_id AND DATE(ngay_tao) = today),
            (SELECT COUNT(*) FROM luot_thich_bai_viet WHERE id_nguoi_dung = user_id AND DATE(ngay_tao) = today),
            (SELECT COUNT(*) FROM luot_thich_bai_viet lk JOIN bai_viet bv ON lk.id_bai_viet = bv.id WHERE bv.id_nguoi_dung = user_id AND DATE(lk.ngay_tao) = today),
            (SELECT COUNT(*) FROM tuong_tac_profile WHERE id_profile_duoc_xem = user_id AND DATE(ngay_tao) = today),
            (SELECT COUNT(*) FROM tin_nhan WHERE id_nguoi_gui = user_id AND DATE(ngay_tao) = today),
            0; -- Thời gian online sẽ được cập nhật riêng
    ELSE
        -- Cập nhật bản ghi hiện có
        UPDATE phan_tich_hanh_vi
        SET 
            so_bai_viet_dang = (SELECT COUNT(*) FROM bai_viet WHERE id_nguoi_dung = user_id AND DATE(ngay_tao) = today),
            so_binh_luan = (SELECT COUNT(*) FROM binh_luan WHERE id_nguoi_dung = user_id AND DATE(ngay_tao) = today),
            so_like_gui = (SELECT COUNT(*) FROM luot_thich_bai_viet WHERE id_nguoi_dung = user_id AND DATE(ngay_tao) = today),
            so_like_nhan = (SELECT COUNT(*) FROM luot_thich_bai_viet lk JOIN bai_viet bv ON lk.id_bai_viet = bv.id WHERE bv.id_nguoi_dung = user_id AND DATE(lk.ngay_tao) = today),
            so_profile_xem = (SELECT COUNT(*) FROM tuong_tac_profile WHERE id_profile_duoc_xem = user_id AND DATE(ngay_tao) = today),
            so_tin_nhan_gui = (SELECT COUNT(*) FROM tin_nhan WHERE id_nguoi_gui = user_id AND DATE(ngay_tao) = today)
        WHERE id_nguoi_dung = user_id AND ngay_phan_tich = today;
    END IF;
    
    -- Cập nhật mức độ tương tác và phổ biến
    UPDATE phan_tich_hanh_vi
    SET 
        diem_hoat_dong = so_bai_viet_dang*5 + so_binh_luan*2 + so_like_gui + thoi_gian_online/60,
        diem_tuong_tac_xa_hoi = so_like_nhan*2 + so_profile_xem + so_tin_nhan_gui*3,
        muc_do_tuong_tac = CASE 
            WHEN (so_bai_viet_dang + so_binh_luan + so_like_gui) < 5 THEN 'thap'
            WHEN (so_bai_viet_dang + so_binh_luan + so_like_gui) < 20 THEN 'trung_binh'
            WHEN (so_bai_viet_dang + so_binh_luan + so_like_gui) < 50 THEN 'cao'
            ELSE 'rat_cao'
        END,
        muc_do_pho_bien = CASE 
            WHEN (so_like_nhan + so_profile_xem) < 10 THEN 'thap'
            WHEN (so_like_nhan + so_profile_xem) < 50 THEN 'trung_binh'
            ELSE 'cao'
        END
    WHERE id_nguoi_dung = user_id AND ngay_phan_tich = today;
END //
DELIMITER ;

-- Stored procedure để cập nhật điểm tương tác giữa hai người dùng
DROP PROCEDURE IF EXISTS update_interaction_points;
DELIMITER //
CREATE PROCEDURE update_interaction_points(IN user_id1 INT, IN user_id2 INT)
BEGIN
    DECLARE total_points INT;
    DECLARE friendship_level VARCHAR(20);
    
    -- Tính tổng điểm tương tác
    SELECT COALESCE(SUM(diem_tuong_tac), 0) INTO total_points
    FROM chi_tiet_tuong_tac 
    WHERE (id_nguoi_1 = user_id1 AND id_nguoi_2 = user_id2) 
       OR (id_nguoi_1 = user_id2 AND id_nguoi_2 = user_id1);
    
    -- Xác định mức độ thân thiết
    SET friendship_level = CASE 
        WHEN total_points < 50 THEN 'xa_la'
        WHEN total_points < 200 THEN 'quen_biet'
        WHEN total_points < 500 THEN 'ban_be'
        ELSE 'than_thiet'
    END;
    
    -- Cập nhật hoặc tạo mới bản ghi điểm tương tác
    IF EXISTS (SELECT 1 FROM diem_tuong_tac WHERE id_nguoi_1 = user_id1 AND id_nguoi_2 = user_id2) THEN
        UPDATE diem_tuong_tac
        SET 
            tong_diem = total_points,
            muc_do_than_thiet = friendship_level,
            lan_tuong_tac_cuoi = NOW(),
            lan_cap_nhat_cuoi = NOW()
        WHERE id_nguoi_1 = user_id1 AND id_nguoi_2 = user_id2;
    ELSE
        INSERT INTO diem_tuong_tac (
            id_nguoi_1, 
            id_nguoi_2, 
            tong_diem, 
            muc_do_than_thiet, 
            lan_tuong_tac_cuoi,
            so_lan_tuong_tac
        ) VALUES (
            user_id1,
            user_id2,
            total_points,
            friendship_level,
            NOW(),
            (SELECT COUNT(*) FROM chi_tiet_tuong_tac WHERE (id_nguoi_1 = user_id1 AND id_nguoi_2 = user_id2) OR (id_nguoi_1 = user_id2 AND id_nguoi_2 = user_id1))
        );
    END IF;
END //
DELIMITER ; 