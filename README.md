# Tugas Akhir Basis Data

### Dokumentasi git

    - Video intalasi git : https://youtu.be/LPa6VOVUcIE
    - Cek versi git : git --version
    - Config git :
        1. git config --global user.name "username github"
        2. git config --global user.email "email github"

### Fork repo
    1. Buat folder baru
    2. git init
    3. git clone https://github.com/LeonardTarigan/tikuzo.git
    4. git remote add upstream https://github.com/LeonardTarigan/tikuzo.git
    5 git pull

### Commit changes
    1. git add .
    2. git commit -m "pesan commit"
    3. git push
   
### Requirements to connect to a database: (DOWNLOAD JDBC / JTDS DRIVER AND SET THE CLASSPATH TO THESE DRIVER)
    1. https://learn.microsoft.com/en-us/sql/sql-server/install/configure-the-windows-firewall-to-allow-sql-server-access?view=sql-server-ver15
    2. https://learn.microsoft.com/en-us/sql/database-engine/configure-windows/configure-a-windows-firewall-for-database-engine-access?view=sql-server-ver15
    3. Buka Sql Server Configuration Manager, pastikan SQL Server Browser aktif dan klik SQL Server Network Configuration
    4. Pilih instance Sql Server yang kalian buat dan buka Properties TCP/IP dan isi "1433" di seluruh field port di bagian IP Address.
    5. Enable protokol TCP/IP. (optional : nyalakan seluruh protokol).
    6. Buat Login baru dengan Microsoft SQL Server Management Studio dengan https://www.ibm.com/docs/en/capmp/8.1.4?topic=monitoring-creating-user-granting-permissions
    (Optional : Buat User-Defined Server Role dan set ke para login baru).

###Tutorial : https://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html

###Reporting : https://community.jaspersoft.com/project/jaspersoft-studio 
               https://community.jaspersoft.com/project/jasperreports-library
               
