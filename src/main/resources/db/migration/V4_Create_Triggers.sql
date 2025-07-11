-- Trigger cập nhật số lượt thích bài viết
DROP TRIGGER IF EXISTS update_bai_viet_like_count;

CREATE TRIGGER update_bai_viet_like_count 
AFTER INSERT ON luot_thich_bai_viet
FOR EACH ROW
BEGIN
    -- Cập nhật số lượt thích
    UPDATE bai_viet 
    SET so_luot_thich = (
        SELECT COUNT(*) 
        FROM luot_thich_bai_viet 
        WHERE id_bai_viet = NEW.id_bai_viet 
        AND trang_thai_thich = 1
    )
    WHERE id = NEW.id_bai_viet;

    -- Thêm vào bảng chi_tiet_tuong_tac
    INSERT INTO chi_tiet_tuong_tac (
        id_nguoi_1,
        id_nguoi_2,
        loai_tuong_tac,
        id_bai_viet,
        diem_tuong_tac,
        nguon_tuong_tac
    )
    SELECT 
        NEW.id_nguoi_dung,
        bv.id_nguoi_dung,
        'like_bai_viet',
        NEW.id_bai_viet,
        10,
        'news_feed'
    FROM bai_viet bv
    WHERE bv.id = NEW.id_bai_viet;
END;

-- Trigger cập nhật số lượt thích khi hủy thích
DROP TRIGGER IF EXISTS update_bai_viet_unlike_count;

CREATE TRIGGER update_bai_viet_unlike_count 
AFTER UPDATE ON luot_thich_bai_viet
FOR EACH ROW
BEGIN
    IF NEW.trang_thai_thich = 0 AND OLD.trang_thai_thich = 1 THEN
        -- Cập nhật số lượt thích
        UPDATE bai_viet 
        SET so_luot_thich = (
            SELECT COUNT(*) 
            FROM luot_thich_bai_viet 
            WHERE id_bai_viet = NEW.id_bai_viet 
            AND trang_thai_thich = 1
        )
        WHERE id = NEW.id_bai_viet;

        -- Thêm vào bảng chi_tiet_tuong_tac
        INSERT INTO chi_tiet_tuong_tac (
            id_nguoi_1,
            id_nguoi_2,
            loai_tuong_tac,
            id_bai_viet,
            diem_tuong_tac,
            nguon_tuong_tac
        )
        SELECT 
            NEW.id_nguoi_dung,
            bv.id_nguoi_dung,
            'unlike_bai_viet',
            NEW.id_bai_viet,
            -5,
            'news_feed'
        FROM bai_viet bv
        WHERE bv.id = NEW.id_bai_viet;
    END IF;
END;

-- Trigger cập nhật số bình luận của bài viết
DROP TRIGGER IF EXISTS update_bai_viet_comment_count;

CREATE TRIGGER update_bai_viet_comment_count 
AFTER INSERT ON binh_luan
FOR EACH ROW
BEGIN
    -- Cập nhật số lượt bình luận
    UPDATE bai_viet 
    SET so_luot_binh_luan = (
        SELECT COUNT(*) 
        FROM binh_luan 
        WHERE id_bai_viet = NEW.id_bai_viet
    )
    WHERE id = NEW.id_bai_viet;

    -- Thêm vào bảng chi_tiet_tuong_tac
    INSERT INTO chi_tiet_tuong_tac (
        id_nguoi_1,
        id_nguoi_2,
        loai_tuong_tac,
        id_bai_viet,
        id_binh_luan,
        diem_tuong_tac,
        nguon_tuong_tac
    )
    SELECT 
        NEW.id_nguoi_dung,
        bv.id_nguoi_dung,
        CASE 
            WHEN NEW.id_binh_luan_cha IS NULL THEN 'binh_luan'
            ELSE 'tra_loi_binh_luan'
        END,
        NEW.id_bai_viet,
        NEW.id,
        15,
        'news_feed'
    FROM bai_viet bv
    WHERE bv.id = NEW.id_bai_viet;
END;

-- Trigger để cập nhật số bạn bè khi kết bạn
DROP TRIGGER IF EXISTS update_friend_count;

CREATE TRIGGER update_friend_count AFTER UPDATE ON ket_ban
FOR EACH ROW
BEGIN
    IF NEW.trang_thai = 'ban_be' AND OLD.trang_thai != 'ban_be' THEN
        UPDATE nguoi_dung 
        SET so_ban_be = so_ban_be + 1
        WHERE id = NEW.id_nguoi_gui OR id = NEW.id_nguoi_nhan;
    END IF;
END;

-- Trigger để cập nhật số thành viên cuộc trò chuyện
DROP TRIGGER IF EXISTS update_chat_member_count;
CREATE TRIGGER update_chat_member_count AFTER INSERT ON thanh_vien_cuoc_tro_chuyen
FOR EACH ROW
BEGIN
    UPDATE cuoc_tro_chuyen 
    SET so_thanh_vien = (SELECT COUNT(*) FROM thanh_vien_cuoc_tro_chuyen WHERE id_cuoc_tro_chuyen = NEW.id_cuoc_tro_chuyen)
    WHERE id = NEW.id_cuoc_tro_chuyen;
END;


-- Trigger để cập nhật số thành viên khi xóa thành viên
DROP TRIGGER IF EXISTS update_chat_member_delete;
CREATE TRIGGER update_chat_member_delete AFTER DELETE ON thanh_vien_cuoc_tro_chuyen
FOR EACH ROW
BEGIN
    UPDATE cuoc_tro_chuyen 
    SET so_thanh_vien = so_thanh_vien - 1
    WHERE id = OLD.id_cuoc_tro_chuyen;
END;
