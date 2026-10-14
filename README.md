# Lá Chắn Cuộc Gọi (SpamShieldVN)

Ứng dụng Android viết bằng Kotlin, Gradle và Jetpack Compose. Ứng dụng cho phép tạo một thương hiệu/nhóm, gắn nhiều số hoặc mẫu đầu số vào nhóm đó, rồi chặn cuộc gọi đến khớp quy tắc bằng API chính thức `CallScreeningService`.

## Chức năng

- Yêu cầu vai trò **Nhận diện và chặn cuộc gọi** bằng màn hình xác nhận chính thức của Android.
- Thêm tên thương hiệu/nhóm cùng nhiều số điện thoại.
- Hỗ trợ số chính xác (`0901234567`), đầu số (`0247300*`), đuôi số (`*4567`) và chứa chuỗi (`*7300*`).
- Tự quy đổi dạng số Việt Nam `+84...` và `0...` khi so khớp.
- Bật/tắt hoặc xóa từng nhóm.
- Lưu tối đa 100 cuộc gọi đã chặn ngay trên máy.
- Không có quyền Internet, không tải danh bạ/lịch sử lên máy chủ.

> Giới hạn Android: dịch vụ sàng lọc nhận được **số điện thoại**, không nhận tên thương hiệu đang được ứng dụng Điện thoại hiển thị. Vì vậy tên thương hiệu trong ứng dụng dùng để nhóm nhiều mẫu số. Muốn chỉ nhập tên rồi tự tìm tất cả số, sản phẩm thực tế cần thêm một cơ sở dữ liệu số-thương-hiệu từ máy chủ.

## Yêu cầu môi trường

- Android Studio hoặc Android SDK có Android Platform 35 và Build Tools 35.0.0.
- JDK 17 trở lên.
- Thiết bị Android 10 (API 29) trở lên.

Sau khi clone, mở thư mục project bằng Android Studio. Android Studio sẽ tự tạo `local.properties` trỏ tới SDK trên máy. File này mang đường dẫn riêng của từng máy nên không được đưa lên Git.

## Build APK

Mở Terminal hoặc PowerShell tại thư mục project:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

Trên macOS/Linux:

```bash
./gradlew testDebugUnitTest assembleDebug
```

APK debug được tạo tại:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Bật USB debugging trên điện thoại

1. Mở **Cài đặt > Giới thiệu điện thoại**.
2. Nhấn liên tục 7 lần vào **Số bản dựng / Build number**. Với Samsung: **Thông tin phần mềm > Số hiệu bản tạo**. Nhập PIN nếu được hỏi.
3. Quay lại **Cài đặt > Hệ thống > Tùy chọn nhà phát triển**. Với Samsung/Xiaomi/OPPO, mục **Tùy chọn nhà phát triển** thường nằm gần cuối trang Cài đặt hoặc trong **Cài đặt bổ sung**.
4. Bật **Gỡ lỗi USB / USB debugging**.
5. Cắm cáp USB có truyền dữ liệu, mở khóa màn hình. Chọn chế độ USB **Truyền tệp (MTP)** nếu máy không nhận.
6. Khi điện thoại hỏi dấu vân tay RSA, chọn **Luôn cho phép từ máy tính này** rồi **Cho phép**.

Kiểm tra kết nối:

```powershell
adb devices -l
```

- Có chữ `device`: đã sẵn sàng.
- Có chữ `unauthorized`: mở khóa điện thoại và chấp nhận hộp thoại RSA.
- Không thấy thiết bị: đổi cáp/cổng USB, chọn Truyền tệp, hoặc cài USB driver của hãng điện thoại.

## Cài và chạy demo trên điện thoại

Sau khi điện thoại hiện là `device`, build và cài APK:

```powershell
.\gradlew.bat assembleDebug
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n "vn.spamshield/.MainActivity"
```

Trong lần mở đầu:

1. Nhấn **Kích hoạt ngay**.
2. Chọn **Lá Chắn Cuộc Gọi** làm ứng dụng nhận diện/chặn spam và xác nhận.
3. Nhấn `+`, nhập tên nhóm và các số/mẫu số, ví dụ `0247300*`.
4. Dùng một số điện thoại khác gọi thử. Không dùng số khẩn cấp; nên thử bằng một số chưa lưu trong Danh bạ.

Android chỉ chuyển các số chưa lưu trong Danh bạ cho dịch vụ sàng lọc nếu ứng dụng không xin quyền đọc danh bạ. Đây là chủ ý an toàn của bản demo.

## Đưa bài lên GitHub và Google Drive

Tạo repository trống tên `SpamShieldVN` trong tài khoản GitHub `Tngoclinh`. Không chọn tạo sẵn README, `.gitignore` hoặc license vì project đã có các file này. Sau đó chạy:

```powershell
cd D:\extraproject\SpamShieldVN
git remote add origin https://github.com/Tngoclinh/SpamShieldVN.git
git push -u origin main
```

Nếu remote `origin` đã tồn tại, thay lệnh `remote add` bằng:

```powershell
git remote set-url origin https://github.com/Tngoclinh/SpamShieldVN.git
git push -u origin main
```

Tải `app-debug.apk` lên Google Drive, chọn **Chia sẻ > Bất kỳ ai có đường liên kết > Người xem**, rồi gửi email gồm link GitHub và link APK cho giảng viên.

## Thiết kế kỹ thuật

`CallScreeningService` phải phản hồi Android trong 5 giây. Vì thế quy tắc được lưu cục bộ bằng `SharedPreferences` và so khớp ngay, không gọi mạng. Khi khớp, app từ chối cuộc gọi, bỏ thông báo cuộc gọi nhỡ của app, nhưng vẫn để Android ghi mục bị chặn vào nhật ký hệ thống.
