use TIKUZO;

GO
DELETE FROM customer
DELETE FROM bioskop
DELETE FROM film
DELETE FROM jadwal
DELETE FROM jadwal_film
DELETE FROM jadwal_studio
DELETE FROM pembayaran
DELETE FROM pesanan
DELETE FROM kota_provinsi
DELETE FROM tiket
DELETE FROM studio
DELETE FROM kursi_studio
GO

ALTER TABLE customer ADD CONSTRAINT UC_email UNIQUE (email);

INSERT INTO customer (customer_ID, nama, email, no_hp, password) VALUES
	('UX012931','Toyama Nao','nao@gmaill.com','089273648299','tnao6128'),
	('CG932842','Kiriko','kiriko@gmail.com','088123942349','krk123'),
	('GF128310','Lucio Santos','snts@gmail.com','081231287824','santossss11'),
	('BB912301','Yilong Ma','ma@gmail.com','081224777623','may77');

INSERT INTO film (film_ID, durasi, genre, judul, sinopsis) VALUES
	('FF123812',120,'Action','Black Panther','The people of Wakanda fight to protect their home from intervening world powers as they mourn the death of King T''Challa.'),
	('GRT123112',130,'Action','Black Adam','Nearly 5,000 years after he was bestowed with the almighty powers of the Egyptian gods--and imprisoned just as quickly--Black Adam is freed from his earthly tomb, ready to unleash his unique form of justice on the modern world.'),
	('KO091823',210,'Thriller','Barbarian','A woman staying at an Airbnb discovers that the house she has rented is not what it seems.'),
	('JFK123130',150,'Adventure','One Piece Film: Red','For the first time ever, Uta - the most beloved singer in the world - will reveal herself to the world at a live concert. The voice that the whole world has been waiting for is about to resound.');

INSERT INTO jadwal(jadwal_ID, tanggal, jam_mulai) VALUES
	('JAN123123','2022-12-12','10:00'),
	('JAK234553','2022-04-03','19:00'),
	('USY234324','2022-04-15','21:30'),
	('OPQ123123','2022-05-20','15:00');

INSERT INTO kota_provinsi(kota, provinsi) VALUES
	('Jakarta','DKI Jakarta'),
	('Bandung','Jawa Barat'),
	('Bogor','Jawa Barat'),
	('Surabaya','Jawa Timur');

INSERT INTO bioskop(nama, brand, jalan, kota) VALUES
	('CGV Jakarta','CGV','Jl. Gunung Tinggi No. 23','Jakarta'),
	('XXI Bandung','XXI','Jl. Pahlawan No. 19','Bandung'),
	('XXI Bogor','XXI','Jl. Ubi No. 33','Bogor'),
	('CGV Surabaya','CGV','Jl. Durian No. 21','Surabaya');

INSERT INTO studio(studio_ID, tipe, nama) VALUES
	('VIP123123','VIP','CGV Jakarta'),
	('REG123801','Reguler','XXI Bandung'),
	('REG123123','Reguler','XXI Bogor'),
	('REG981230','Reguler','CGV Surabaya');

INSERT INTO kursi_studio(studio_ID, no_kursi, status) VALUES
	('VIP123123','1A',1),
	('VIP123123','1B',0),
	('REG123801','1C',0),
	('REG123801','1D',1);

INSERT INTO jadwal_film(film_ID, jadwal_ID) VALUES
	('FF123812', 'JAN123123'),
	('GRT123112', 'JAK234553'),
	('KO091823', 'USY234324'),
	('JFK123130', 'OPQ123123');

INSERT INTO jadwal_studio(jadwal_ID, studio_ID) VALUES
	('JAN123123', 'VIP123123'),
	('JAK234553', 'VIP123123'),
	('USY234324', 'REG123801'),
	('OPQ123123', 'REG123801');

INSERT INTO pesanan(pesanan_ID, customer_ID) VALUES 
	('ORD00000001', 'UX012931'),
	('ORD00000002', 'CG932842'),
	('ORD00000003', 'GF128310'),
	('ORD00000004', 'BB912301');

INSERT INTO tiket(tiket_ID, harga, jadwal_ID, pesanan_ID) VALUES
	('FC1230123', 40000, 'JAN123123', 'ORD00000001'),
	('GXG897139', 50000, 'JAK234553', 'ORD00000002'),
	('HA1230130', 40000, 'USY234324', 'ORD00000003'),
	('VH1328172', 40000, 'OPQ123123', 'ORD00000004');

INSERT INTO pembayaran(pembayaran_ID, status, metode, pesanan_ID) VALUES
	('CST0000001', 1, 'Bank BCA', 'ORD00000001'),
	('CST0000002', 1, 'Dana', 'ORD00000002'),
	('CST0000003', 0, 'OVO', 'ORD00000003'),
	('CST0000004', 0, 'Bank BNI', 'ORD00000004');