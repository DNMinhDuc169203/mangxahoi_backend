CREATE TABLE tin_nhan_da_doc (
  id INT NOT NULL AUTO_INCREMENT,
  id_tin_nhan INT NOT NULL,
  id_nguoi_doc INT NOT NULL,
  thoi_gian_doc TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY unique_doc (id_tin_nhan, id_nguoi_doc),
  FOREIGN KEY (id_tin_nhan) REFERENCES tin_nhan(id) ON DELETE CASCADE,
  FOREIGN KEY (id_nguoi_doc) REFERENCES nguoi_dung(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 