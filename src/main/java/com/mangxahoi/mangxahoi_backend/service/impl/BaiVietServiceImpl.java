package com.mangxahoi.mangxahoi_backend.service.impl;

import com.mangxahoi.mangxahoi_backend.dto.BaiVietDTO;
import com.mangxahoi.mangxahoi_backend.entity.*;
import com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet;
import com.mangxahoi.mangxahoi_backend.enums.LoaiMedia;
import com.mangxahoi.mangxahoi_backend.exception.ResourceNotFoundException;
import com.mangxahoi.mangxahoi_backend.repository.*;
import com.mangxahoi.mangxahoi_backend.service.BaiVietService;
import com.mangxahoi.mangxahoi_backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    @Transactional
    public BaiVietDTO taoBaiViet(Integer idNguoiDung, BaiVietDTO baiVietDTO, List<MultipartFile> media) {
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
                
                // Tăng số lượt thích của bài viết
                baiViet.setSoLuotThich(baiViet.getSoLuotThich() + 1);
                baiVietRepository.save(baiViet);
                
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
            
            return true;
        }
        
        // Nếu chưa thích hoặc đã bỏ thích rồi, không làm gì cả
        return false;
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
} 