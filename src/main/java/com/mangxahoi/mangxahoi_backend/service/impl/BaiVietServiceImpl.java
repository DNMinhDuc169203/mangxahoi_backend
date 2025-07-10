package com.mangxahoi.mangxahoi_backend.service.impl;

import com.mangxahoi.mangxahoi_backend.dto.BaiVietDTO;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.entity.*;
import com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet;
import com.mangxahoi.mangxahoi_backend.enums.LoaiMedia;
import com.mangxahoi.mangxahoi_backend.enums.LoaiThongBao;
import com.mangxahoi.mangxahoi_backend.exception.ResourceNotFoundException;
import com.mangxahoi.mangxahoi_backend.repository.*;
import com.mangxahoi.mangxahoi_backend.service.BaiVietService;
import com.mangxahoi.mangxahoi_backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaiVietServiceImpl implements BaiVietService {

    private final BaiVietRepository baiVietRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final BaiVietMediaRepository baiVietMediaRepository;
    private final HashtagRepository hashtagRepository;
    private final BaiVietHashtagRepository baiVietHashtagRepository;
    private final LuotThichBaiVietRepository luotThichBaiVietRepository;
    private final CloudinaryService cloudinaryService;
    private final KetBanRepository ketBanRepository;
    private final ThongBaoRepository thongBaoRepository;
    private final LichSuXuLyBaiVietRepository lichSuXuLyBaiVietRepository;
    private final ChinhSachRepository chinhSachRepository;

    @Override
    @Transactional
    public BaiVietDTO taoBaiViet(Integer idNguoiDung, BaiVietDTO baiVietDTO, List<MultipartFile> media) {
        // Kiểm tra từ khóa cấm (lấy từ chính sách mới nhất nếu có)
        String noiDung = baiVietDTO.getNoiDung();
        String tuKhoaCam = null;
        com.mangxahoi.mangxahoi_backend.entity.ChinhSach chinhSach = chinhSachRepository.findTopByOrderByNgayCapNhatDesc();
        if (chinhSach != null && chinhSach.getNoiDung() != null) {
            // Giả sử các từ khóa cấm nằm trong đoạn: "TỪ KHÓA CẤM: ..." (cách nhau bởi dấu phẩy)
            String[] lines = chinhSach.getNoiDung().split("\n");
            for (String line : lines) {
                if (line.trim().toLowerCase().startsWith("từ khóa cấm:")) {
                    String[] tuKhoaArr = line.substring(line.indexOf(":") + 1).split(",");
                    for (String tk : tuKhoaArr) {
                        if (noiDung != null && noiDung.toLowerCase().contains(tk.trim().toLowerCase())) {
                            tuKhoaCam = tk.trim();
                            break;
                        }
                    }
                }
            }
        }
        if (tuKhoaCam != null) {
            throw new RuntimeException("Nội dung bài viết chứa từ khóa bị cấm theo chính sách: '" + tuKhoaCam + "'. Vui lòng chỉnh sửa!");
        }
        // Tìm người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", idNguoiDung));

        // Tạo bài viết mới
        BaiViet baiViet = new BaiViet();
        baiViet.setNguoiDung(nguoiDung);
        baiViet.setNoiDung(baiVietDTO.getNoiDung());
        baiViet.setCheDoRiengTu(baiVietDTO.getCheDoRiengTu() != null ? baiVietDTO.getCheDoRiengTu() : CheDoBaiViet.cong_khai);

        // Lưu bài viết
        BaiViet savedBaiViet = baiVietRepository.save(baiViet);

        // Xử lý media nếu có
        List<String> mediaUrls = new ArrayList<>();
        if (media != null && !media.isEmpty()) {
            for (MultipartFile file : media) {
                try {
                    // Upload lên Cloudinary
                    String mediaUrl = cloudinaryService.uploadFile(file, "bai_viet_media");
                    
                    // Xác định loại media
                    LoaiMedia loaiMedia = file.getContentType().startsWith("image/") ? LoaiMedia.anh : LoaiMedia.video;
                    
                    // Tạo entity BaiVietMedia
                    BaiVietMedia baiVietMedia = new BaiVietMedia();
                    baiVietMedia.setBaiViet(savedBaiViet);
                    baiVietMedia.setUrl(mediaUrl);
                    baiVietMedia.setLoaiMedia(loaiMedia);
                    
                    // Lưu media
                    baiVietMediaRepository.save(baiVietMedia);
                    
                    // Thêm URL vào danh sách
                    mediaUrls.add(mediaUrl);
                } catch (IOException e) {
                    throw new RuntimeException("Lỗi khi upload media: " + e.getMessage());
                }
            }
        }

        // Xử lý hashtags nếu có
        List<String> hashtags = new ArrayList<>();
        if (baiVietDTO.getHashtags() != null && !baiVietDTO.getHashtags().isEmpty()) {
            for (String hashtagName : baiVietDTO.getHashtags()) {
                // Chuẩn hóa tên hashtag
                String normalizedHashtagName = hashtagName.trim().toLowerCase().replaceAll("\\s+", "");
                
                // Thêm dấu # ở đầu nếu chưa có
                if (!normalizedHashtagName.isEmpty()) {
                    if (!normalizedHashtagName.startsWith("#")) {
                        normalizedHashtagName = "#" + normalizedHashtagName;
                    }
                    
                    // Lưu tham chiếu đến bài viết và hashtag trong biến final để sử dụng trong lambda
                    final BaiViet finalBaiViet = savedBaiViet;
                    final String finalHashtagName = normalizedHashtagName;
                    
                    // Tìm hoặc tạo mới hashtag
                    Hashtag hashtag = hashtagRepository.findByTen(normalizedHashtagName)
                            .orElseGet(() -> {
                                Hashtag newHashtag = new Hashtag();
                                newHashtag.setTen(finalHashtagName);
                                newHashtag.setSoLanSuDung(0);
                                return hashtagRepository.save(newHashtag);
                            });
                    
                    // Tăng số lần sử dụng
                    hashtag.setSoLanSuDung(hashtag.getSoLanSuDung() + 1);
                    hashtagRepository.save(hashtag);
                    
                    // Tạo liên kết giữa bài viết và hashtag
                    BaiVietHashtag baiVietHashtag = new BaiVietHashtag();
                    baiVietHashtag.setBaiViet(finalBaiViet);
                    baiVietHashtag.setHashtag(hashtag);
                    baiVietHashtagRepository.save(baiVietHashtag);
                    
                    // Thêm vào danh sách
                    hashtags.add(normalizedHashtagName);
                }
            }
        }

        // Cập nhật số bài đăng của người dùng
        nguoiDung.setSoBaiDang(nguoiDung.getSoBaiDang() + 1);
        nguoiDungRepository.save(nguoiDung);

        // Tạo DTO để trả về
        BaiVietDTO resultDTO = convertToDTO(savedBaiViet);
        resultDTO.setMediaUrls(mediaUrls);
        resultDTO.setHashtags(hashtags);
        resultDTO.setDaThich(false); // Người dùng vừa tạo bài viết nên chưa thích

        return resultDTO;
    }

    @Override
    @Transactional
    public BaiVietDTO capNhatBaiViet(Integer id, BaiVietDTO baiVietDTO, Integer idNguoiDung) {
        // Tìm bài viết
        BaiViet baiViet = baiVietRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", "id", id));

        // Kiểm tra quyền sở hữu
        if (!baiViet.getNguoiDung().getId().equals(idNguoiDung)) {
            throw new SecurityException("Bạn không có quyền cập nhật bài viết này");
        }

        // Cập nhật thông tin
        baiViet.setNoiDung(baiVietDTO.getNoiDung());
        if (baiVietDTO.getCheDoRiengTu() != null) {
            baiViet.setCheDoRiengTu(baiVietDTO.getCheDoRiengTu());
        }

        // Lưu bài viết
        BaiViet updatedBaiViet = baiVietRepository.save(baiViet);

        // Chuyển đổi sang DTO và trả về
        return convertToDTO(updatedBaiViet);
    }

    @Override
    @Transactional
    public void xoaBaiViet(Integer id, Integer idNguoiDung) {
        // Tìm bài viết
        BaiViet baiViet = baiVietRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", "id", id));

        // Kiểm tra quyền sở hữu
        if (!baiViet.getNguoiDung().getId().equals(idNguoiDung)) {
            throw new SecurityException("Bạn không có quyền xóa bài viết này");
        }

        // Xóa các media trên Cloudinary
        List<BaiVietMedia> mediaList = baiVietMediaRepository.findByBaiViet(baiViet);
        for (BaiVietMedia media : mediaList) {
            try {
                // Lấy public_id từ URL
                String url = media.getUrl();
                String publicId = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
                
                // Xóa file trên Cloudinary
                cloudinaryService.deleteFile("bai_viet_media/" + publicId);
            } catch (IOException e) {
                // Ghi log lỗi nhưng vẫn tiếp tục xóa trong database
                System.err.println("Lỗi khi xóa media trên Cloudinary: " + e.getMessage());
            }
        }

        // Giảm số bài đăng của người dùng
        NguoiDung nguoiDung = baiViet.getNguoiDung();
        nguoiDung.setSoBaiDang(Math.max(0, nguoiDung.getSoBaiDang() - 1));
        nguoiDungRepository.save(nguoiDung);

        // Xóa bài viết (cascade sẽ xóa các media, hashtag liên kết)
        baiVietRepository.delete(baiViet);
    }

    @Override
    public Optional<BaiVietDTO> timTheoId(Integer id, Integer idNguoiDungHienTai) {
        // Tìm bài viết
        Optional<BaiViet> baiVietOpt = baiVietRepository.findById(id);
        
        if (baiVietOpt.isPresent()) {
            BaiViet baiViet = baiVietOpt.get();
            
            // Kiểm tra quyền truy cập
            if (kiemTraQuyenTruyCap(baiViet, idNguoiDungHienTai)) {
                // Tăng số lượt xem
                baiViet.setSoLuotXem(baiViet.getSoLuotXem() + 1);
                baiVietRepository.save(baiViet);
                
                // Chuyển đổi sang DTO và trả về
                BaiVietDTO baiVietDTO = convertToDTO(baiViet);
                
                // Kiểm tra người dùng hiện tại đã thích bài viết chưa
                boolean daThich = luotThichBaiVietRepository.findByNguoiDungIdAndBaiVietIdAndTrangThaiThichTrue(
                        idNguoiDungHienTai, id).isPresent();
                baiVietDTO.setDaThich(daThich);
                
                return Optional.of(baiVietDTO);
            }
        }
        
        return Optional.empty();
    }

    @Override
    public Page<BaiVietDTO> timBaiVietCuaNguoiDung(Integer idNguoiDung, Integer idNguoiDungHienTai, Pageable pageable) {
        // Tìm người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", idNguoiDung));
        
        // Lấy danh sách bài viết
        Page<BaiViet> baiVietPage;
        
        // Nếu là chính người dùng đó xem, hiển thị tất cả bài viết
        if (Objects.equals(idNguoiDung, idNguoiDungHienTai)) {
            baiVietPage = baiVietRepository.findByNguoiDung(nguoiDung, pageable);
        } else {
            // Nếu là người khác xem, chỉ hiển thị bài viết công khai
            baiVietPage = baiVietRepository.findByNguoiDungAndCheDoRiengTu(nguoiDung, CheDoBaiViet.cong_khai, pageable);
        }
        
        // Chuyển đổi sang DTO
        return baiVietPage.map(baiViet -> {
            BaiVietDTO dto = convertToDTO(baiViet);
            
            // Kiểm tra người dùng hiện tại đã thích bài viết chưa
            boolean daThich = luotThichBaiVietRepository.findByNguoiDungIdAndBaiVietIdAndTrangThaiThichTrue(
                    idNguoiDungHienTai, baiViet.getId()).isPresent();
            dto.setDaThich(daThich);
            
            return dto;
        });
    }

    @Override
    public Page<BaiVietDTO> timBaiVietCongKhai(Integer idNguoiDungHienTai, Pageable pageable) {
        // Lấy danh sách bài viết công khai
        Page<BaiViet> baiVietPage = baiVietRepository.findAllPublicPosts(pageable);
        
        // Chuyển đổi sang DTO
        return baiVietPage.map(baiViet -> {
            BaiVietDTO dto = convertToDTO(baiViet);
            
            // Kiểm tra người dùng hiện tại đã thích bài viết chưa
            boolean daThich = luotThichBaiVietRepository.findByNguoiDungIdAndBaiVietIdAndTrangThaiThichTrue(
                    idNguoiDungHienTai, baiViet.getId()).isPresent();
            dto.setDaThich(daThich);
            
            return dto;
        });
    }

    @Override
    public Page<BaiVietDTO> timBaiVietXuHuong(Integer idNguoiDungHienTai, Pageable pageable) {
        // Lấy danh sách bài viết xu hướng
        Page<BaiViet> baiVietPage = baiVietRepository.findTrendingPosts(pageable);
        
        // Chuyển đổi sang DTO
        return baiVietPage.map(baiViet -> {
            BaiVietDTO dto = convertToDTO(baiViet);
            
            // Kiểm tra người dùng hiện tại đã thích bài viết chưa
            boolean daThich = luotThichBaiVietRepository.findByNguoiDungIdAndBaiVietIdAndTrangThaiThichTrue(
                    idNguoiDungHienTai, baiViet.getId()).isPresent();
            dto.setDaThich(daThich);
            
            return dto;
        });
    }

    @Override
    public Page<BaiVietDTO> timBaiVietTheoHashtag(String tenHashtag, Integer idNguoiDungHienTai, Pageable pageable) {
        // Chuẩn hóa tên hashtag
        String normalizedHashtagName = tenHashtag.trim().toLowerCase().replaceAll("\\s+", "");
        
        // Lấy danh sách bài viết theo hashtag
        Page<BaiViet> baiVietPage = baiVietRepository.findByHashtag(normalizedHashtagName, pageable);
        
        // Chuyển đổi sang DTO
        return baiVietPage.map(baiViet -> {
            BaiVietDTO dto = convertToDTO(baiViet);
            
            // Kiểm tra người dùng hiện tại đã thích bài viết chưa
            boolean daThich = luotThichBaiVietRepository.findByNguoiDungIdAndBaiVietIdAndTrangThaiThichTrue(
                    idNguoiDungHienTai, baiViet.getId()).isPresent();
            dto.setDaThich(daThich);
            
            return dto;
        });
    }

    @Override
    @Transactional
    public boolean thichBaiViet(Integer idBaiViet, Integer idNguoiDung) {
        // Tìm bài viết
        BaiViet baiViet = baiVietRepository.findById(idBaiViet)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", "id", idBaiViet));
        
        // Tìm người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", idNguoiDung));
        
        // Kiểm tra xem người dùng đã thích bài viết này chưa
        Optional<LuotThichBaiViet> existingLike = luotThichBaiVietRepository.findByNguoiDungAndBaiViet(nguoiDung, baiViet);
        
        if (existingLike.isPresent()) {
            // Nếu đã thích rồi và đang ở trạng thái đã hủy thích, cập nhật lại thành thích
            LuotThichBaiViet luotThich = existingLike.get();
            if (!luotThich.getTrangThaiThich()) {
                luotThich.setTrangThaiThich(true);
                luotThich.setNgayHuyThich(null);
                luotThichBaiVietRepository.save(luotThich);
                baiViet.setSoLuotThich(baiViet.getSoLuotThich() + 1);
                baiVietRepository.save(baiViet);
                // Đã bỏ logic gửi thông báo ở đây
                return true;
            }
            // Nếu đã thích rồi và vẫn đang thích, không làm gì cả
            return false;
        } else {
            // Nếu chưa thích, tạo mới lượt thích
            LuotThichBaiViet luotThich = new LuotThichBaiViet();
            luotThich.setNguoiDung(nguoiDung);
            luotThich.setBaiViet(baiViet);
            luotThich.setTrangThaiThich(true);
            luotThichBaiVietRepository.save(luotThich);
            
            // Tăng số lượt thích của bài viết
            baiViet.setSoLuotThich(baiViet.getSoLuotThich() + 1);
            baiVietRepository.save(baiViet);
            // Đã bỏ logic gửi thông báo ở đây
            return true;
        }
    }

    @Override
    @Transactional
    public boolean boThichBaiViet(Integer idBaiViet, Integer idNguoiDung) {
        // Tìm bài viết
        BaiViet baiViet = baiVietRepository.findById(idBaiViet)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", "id", idBaiViet));
        
        // Tìm người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", idNguoiDung));
        
        // Kiểm tra xem người dùng đã thích bài viết này chưa
        Optional<LuotThichBaiViet> existingLike = luotThichBaiVietRepository.findByNguoiDungAndBaiViet(nguoiDung, baiViet);
        
        if (existingLike.isPresent() && existingLike.get().getTrangThaiThich()) {
            // Nếu đã thích, cập nhật thành bỏ thích
            LuotThichBaiViet luotThich = existingLike.get();
            luotThich.setTrangThaiThich(false);
            luotThich.setNgayHuyThich(java.time.LocalDateTime.now());
            luotThichBaiVietRepository.save(luotThich);
            
            // Giảm số lượt thích của bài viết
            baiViet.setSoLuotThich(Math.max(0, baiViet.getSoLuotThich() - 1));
            baiVietRepository.save(baiViet);
            // XÓA THÔNG BÁO TƯƠNG ỨNG
            thongBaoRepository.findByNguoiNhan(baiViet.getNguoiDung()).stream()
                .filter(tb -> tb.getLoai().equals(LoaiThongBao.tuong_tac.name()) &&
                        tb.getNoiDung() != null &&
                        tb.getNoiDung().contains("Người dùng " + nguoiDung.getHoTen() + " vừa thích bài viết của bạn."))
                .forEach(tb -> thongBaoRepository.delete(tb));
            return true;
        }
        
        // Nếu chưa thích hoặc đã bỏ thích rồi, không làm gì cả
        return false;
    }

    @Override
    public Page<BaiVietDTO> layNewsfeedTongHop(Integer idNguoiDung, Pageable pageable) {
        // Lấy user
        NguoiDung user = nguoiDungRepository.findById(idNguoiDung)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", idNguoiDung));

        // Lấy danh sách bạn bè
        List<KetBan> friends = ketBanRepository.findAllFriends(user);
        List<Integer> friendIds = new java.util.ArrayList<>();
        for (KetBan k : friends) {
            if (k.getNguoiGui().getId().equals(idNguoiDung)) {
                friendIds.add(k.getNguoiNhan().getId());
            } else {
                friendIds.add(k.getNguoiGui().getId());
            }
        }

        // Lấy bài viết công khai
        List<BaiViet> congKhai = baiVietRepository.findAllPublicPosts(PageRequest.of(0, 100)).getContent();
        // Lấy bài viết bạn bè (che_do_rieng_tu = 'ban_be' và id_nguoi_dung in friendIds)
        List<BaiViet> banBe = friendIds.isEmpty() ? new java.util.ArrayList<>() :
            baiVietRepository.findAllByNguoiDungIdInAndCheDoRiengTu(friendIds, com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet.ban_be);
        // Lấy bài viết của chính user
        List<BaiViet> cuaToi = baiVietRepository.findByNguoiDung(user, PageRequest.of(0, 100)).getContent();
        // Lấy bài viết xu hướng
        List<BaiViet> xuHuong = baiVietRepository.findTrendingPosts(PageRequest.of(0, 100)).getContent();

        // Gộp, loại trùng, sắp xếp
        java.util.Set<BaiViet> all = new java.util.HashSet<>();
        all.addAll(congKhai);
        all.addAll(banBe);
        all.addAll(cuaToi);
        all.addAll(xuHuong);
        java.util.List<BaiViet> sorted = all.stream()
            .sorted(java.util.Comparator.comparing(BaiViet::getNgayTao).reversed())
            .toList();

        // Phân trang thủ công
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sorted.size());
        if (start >= sorted.size()) {
            return new org.springframework.data.domain.PageImpl<>(Collections.emptyList(), pageable, sorted.size());
        }
        java.util.List<BaiVietDTO> pageContent = sorted.subList(start, end).stream()
            .map(baiViet -> {
                BaiVietDTO dto = convertToDTO(baiViet);
                boolean daThich = luotThichBaiVietRepository
                    .findByNguoiDungIdAndBaiVietIdAndTrangThaiThichTrue(idNguoiDung, baiViet.getId())
                    .isPresent();
                dto.setDaThich(daThich);
                return dto;
            })
            .toList();

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, sorted.size());
    }

    @Override
    public List<NguoiDungDTO> layDanhSachNguoiThichBaiViet(Integer idBaiViet) {
        BaiViet baiViet = baiVietRepository.findById(idBaiViet)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", "id", idBaiViet));
        
        List<NguoiDung> nguoiDungs = luotThichBaiVietRepository.findNguoiDungsByBaiVietAndTrangThaiThichTrue(baiViet);
        
        return nguoiDungs.stream()
                .map(this::convertToNguoiDungDTO)
                .collect(Collectors.toList());
    }
    
    private NguoiDungDTO convertToNguoiDungDTO(NguoiDung nguoiDung) {
        NguoiDungDTO dto = new NguoiDungDTO();
        dto.setId(nguoiDung.getId());
        dto.setHoTen(nguoiDung.getHoTen());
        dto.setEmail(nguoiDung.getEmail());
        dto.setSoDienThoai(nguoiDung.getSoDienThoai());
        dto.setNgaySinh(nguoiDung.getNgaySinh());
        dto.setGioiTinh(nguoiDung.getGioiTinh());
        dto.setDiaChi(nguoiDung.getDiaChi());
        dto.setTieuSu(nguoiDung.getTieuSu());
        dto.setNgayTao(nguoiDung.getNgayTao());
        dto.setLanDangNhapCuoi(nguoiDung.getLanDangNhapCuoi());
        dto.setVaiTro(nguoiDung.getVaiTro());
        dto.setMucRiengTu(nguoiDung.getMucRiengTu());
        dto.setAnhDaiDien(null);
        dto.setDaXacThuc(nguoiDung.getDaXacThuc());
        dto.setDangHoatDong(nguoiDung.getDangHoatDong());
        dto.setSoBanBe(nguoiDung.getSoBanBe());
        dto.setSoBaiDang(nguoiDung.getSoBaiDang());
        dto.setEmailCongKhai(nguoiDung.getEmailCongKhai());
        dto.setSdtCongKhai(nguoiDung.getSdtCongKhai());
        dto.setNgaySinhCongKhai(nguoiDung.getNgaySinhCongKhai());
        dto.setGioiTinhCongKhai(nguoiDung.getGioiTinhCongKhai());

        if (nguoiDung.getAnhDaiDien() != null && !nguoiDung.getAnhDaiDien().isEmpty()) {
            NguoiDungAnh anhChinh = nguoiDung.getAnhDaiDien().stream()
                .filter(NguoiDungAnh::getLaAnhChinh)
                .findFirst()
                .orElse(nguoiDung.getAnhDaiDien().get(0));
            dto.setAnhDaiDien(anhChinh.getUrl());
        }

        return dto;
    }

    /**
     * Chuyển đổi entity BaiViet sang DTO
     * 
     * @param baiViet Entity BaiViet
     * @return BaiVietDTO
     */
    private BaiVietDTO convertToDTO(BaiViet baiViet) {
        BaiVietDTO dto = new BaiVietDTO();
        dto.setId(baiViet.getId());
        dto.setIdNguoiDung(baiViet.getNguoiDung().getId());
        dto.setHoTenNguoiDung(baiViet.getNguoiDung().getHoTen());
 
        
        // Lấy ảnh đại diện của người dùng nếu có
        if (baiViet.getNguoiDung().getAnhDaiDien() != null) {
            baiViet.getNguoiDung().getAnhDaiDien().stream()
                    .filter(NguoiDungAnh::getLaAnhChinh)
                    .findFirst()
                    .ifPresent(anh -> dto.setAnhDaiDienNguoiDung(anh.getUrl()));
        }
        
        dto.setNoiDung(baiViet.getNoiDung());
        dto.setCheDoRiengTu(baiViet.getCheDoRiengTu());
        dto.setDangXuHuong(baiViet.getDangXuHuong());
        dto.setSoLuotXem(baiViet.getSoLuotXem());
        dto.setSoLuotThich(baiViet.getSoLuotThich());
        dto.setSoLuotBinhLuan(baiViet.getSoLuotBinhLuan());
        dto.setNgayTao(baiViet.getNgayTao());
        dto.setNgayCapNhat(baiViet.getNgayCapNhat());
        
        // Lấy danh sách URL media
        List<String> mediaUrls = baiVietMediaRepository.findByBaiViet(baiViet).stream()
                .map(BaiVietMedia::getUrl)
                .collect(Collectors.toList());
        dto.setMediaUrls(mediaUrls);
        
        // Lấy danh sách hashtag
        List<String> hashtags = new ArrayList<>();
        if (baiViet.getHashtags() != null) {
            hashtags = baiViet.getHashtags().stream()
                    .map(Hashtag::getTen)
                    .collect(Collectors.toList());
        }
        dto.setHashtags(hashtags);
        dto.setBiAn(baiViet.getBiAn());
        
        return dto;
    }

    /**
     * Kiểm tra quyền truy cập vào bài viết
     * 
     * @param baiViet Bài viết cần kiểm tra
     * @param idNguoiDungHienTai ID của người dùng hiện tại
     * @return true nếu có quyền truy cập, false nếu không
     */
    private boolean kiemTraQuyenTruyCap(BaiViet baiViet, Integer idNguoiDungHienTai) {
        // Nếu bài viết công khai, ai cũng có thể xem
        if (baiViet.getCheDoRiengTu() == CheDoBaiViet.cong_khai) {
            return true;
        }
        
        // Nếu người xem là chủ bài viết, có thể xem
        if (Objects.equals(baiViet.getNguoiDung().getId(), idNguoiDungHienTai)) {
            return true;
        }
        
        // TODO: Nếu bài viết ở chế độ bạn bè, kiểm tra xem người xem có phải là bạn bè không
        // Hiện tại chưa có logic kiểm tra bạn bè, sẽ bổ sung sau
        
        return false;
    }

    @Override
    @Transactional
    public void anBaiVietByAdmin(Integer idBaiViet, Integer adminId, String lyDo) {
        BaiViet baiViet = baiVietRepository.findById(idBaiViet)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", "id", idBaiViet));
        baiViet.setBiAn(true);
        baiViet.setLyDoAn(lyDo);
        baiVietRepository.save(baiViet);
        // Ghi lịch sử xử lý
        LichSuXuLyBaiViet ls = new LichSuXuLyBaiViet();
        ls.setBaiViet(baiViet);
        ls.setHanhDong("an");
        ls.setThoiGian(java.time.LocalDateTime.now());
        ls.setLyDo(lyDo);
        ls.setAdminXuLy(nguoiDungRepository.findById(adminId).orElse(null));
        lichSuXuLyBaiVietRepository.save(ls);
    }

    @Override
    @Transactional
    public void hienBaiVietByAdmin(Integer idBaiViet, Integer adminId) {
        BaiViet baiViet = baiVietRepository.findById(idBaiViet)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", "id", idBaiViet));
        baiViet.setBiAn(false);
        baiViet.setLyDoAn(null);
        baiVietRepository.save(baiViet);
        // Ghi lịch sử xử lý
        LichSuXuLyBaiViet ls = new LichSuXuLyBaiViet();
        ls.setBaiViet(baiViet);
        ls.setHanhDong("hien");
        ls.setThoiGian(java.time.LocalDateTime.now());
        ls.setLyDo(null);
        ls.setAdminXuLy(nguoiDungRepository.findById(adminId).orElse(null));
        lichSuXuLyBaiVietRepository.save(ls);
    }

    @Override
    @Transactional
    public void xoaBaiVietByAdmin(Integer idBaiViet, Integer adminId, String lyDo) {
        BaiViet baiViet = baiVietRepository.findById(idBaiViet)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", "id", idBaiViet));
        // Ghi lịch sử xử lý
        LichSuXuLyBaiViet ls = new LichSuXuLyBaiViet();
        ls.setBaiViet(baiViet);
        ls.setHanhDong("xoa");
        ls.setThoiGian(java.time.LocalDateTime.now());
        ls.setLyDo(lyDo);
        ls.setAdminXuLy(nguoiDungRepository.findById(adminId).orElse(null));
        lichSuXuLyBaiVietRepository.save(ls);
        baiVietRepository.delete(baiViet);
    }

    @Override
    public Page<BaiVietDTO> timKiemBaiVietAdmin(String keyword, String hashtag, String trangThai, String loai, Boolean sensitive, Pageable pageable) {
        // Lấy tất cả bài viết (có thể tối ưu bằng query động nếu cần)
        List<BaiViet> all = baiVietRepository.findAll();
        List<String> tuKhoaNhayCam = List.of("sex", "bạo lực", "đồi trụy", "nhạy cảm"); // ví dụ
        List<BaiViet> filtered = all.stream()
            // Tìm kiếm theo nội dung
            .filter(bv -> keyword == null || bv.getNoiDung().toLowerCase().contains(keyword.toLowerCase()))
            // Lọc theo hashtag (dựa vào bảng liên kết hashtag, tìm gần đúng)
            .filter(bv -> hashtag == null || (bv.getHashtags() != null && bv.getHashtags().stream().anyMatch(h -> h.getTen().toLowerCase().contains(hashtag.toLowerCase()))))
            // Lọc theo trạng thái: binh_thuong, an, xoa
            .filter(bv -> {
                if (trangThai == null || trangThai.isEmpty()) return true;
                if (trangThai.equals("an")) return Boolean.TRUE.equals(bv.getBiAn());
                if (trangThai.equals("binh_thuong")) return !Boolean.TRUE.equals(bv.getBiAn());
                // Nếu muốn lọc đã xóa, cần bổ sung trường trạng thái xóa (hiện tại xóa là xóa cứng)
                return true;
            })
            // Lọc theo loại: hashtag, thong_thuong
            .filter(bv -> {
                if (loai == null || loai.isEmpty()) return true;
                if (loai.equals("hashtag")) return bv.getHashtags() != null && !bv.getHashtags().isEmpty();
                if (loai.equals("thong_thuong")) return bv.getHashtags() == null || bv.getHashtags().isEmpty();
                return true;
            })
            // Lọc theo nhạy cảm
            .filter(bv -> {
                if (sensitive == null) return true;
                boolean coNhayCam = bv.getNoiDung() != null && tuKhoaNhayCam.stream().anyMatch(tk -> bv.getNoiDung().toLowerCase().contains(tk));
                return sensitive ? coNhayCam : !coNhayCam;
            })
            .collect(java.util.stream.Collectors.toList());
        List<BaiVietDTO> dtos = filtered.stream().map(this::convertToDTO).collect(java.util.stream.Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        return new org.springframework.data.domain.PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    @Override
    public List<LichSuXuLyBaiViet> lichSuXuLyBaiViet(Integer idBaiViet) {
        BaiViet baiViet = baiVietRepository.findById(idBaiViet)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", "id", idBaiViet));
        return lichSuXuLyBaiVietRepository.findByBaiViet(baiViet);
    }

    @Override
    public Map<String, Object> thongKeBaiViet(String fromDate, String toDate) {
        List<BaiViet> all = baiVietRepository.findAll();
        List<String> tuKhoaNhayCam = List.of("sex", "bạo lực", "đồi trụy", "nhạy cảm");
        long tongSo = all.size();
        long soBiAn = all.stream().filter(bv -> Boolean.TRUE.equals(bv.getBiAn())).count();
        long soNhayCam = all.stream().filter(bv -> bv.getNoiDung() != null && tuKhoaNhayCam.stream().anyMatch(tk -> bv.getNoiDung().toLowerCase().contains(tk))).count();
        // Số bị xóa: đếm trong lịch sử xử lý
        long soBiXoa = lichSuXuLyBaiVietRepository.findAll().stream().filter(ls -> "xoa".equals(ls.getHanhDong())).count();
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("tongSo", tongSo);
        result.put("soBiAn", soBiAn);
        result.put("soBiXoa", soBiXoa);
        result.put("soNhayCam", soNhayCam);
        return result;
    }

    @Override
    public List<BaiVietDTO> findTop5MoiNhat() {
        List<BaiViet> list = baiVietRepository.findTop5ByOrderByNgayTaoDesc(PageRequest.of(0, 5));
        return list.stream().map(this::convertToDTO).toList();
    }
} 