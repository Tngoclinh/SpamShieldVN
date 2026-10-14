# Lá Chắn Cuộc Gọi (SpamShieldVN)

**Lá Chắn Cuộc Gọi** là ứng dụng Android giúp bạn tự tạo danh sách số điện thoại cần chặn, sắp xếp theo tên thương hiệu hoặc nhóm. Ứng dụng phù hợp khi bạn thường xuyên nhận cuộc gọi từ những số có cùng đầu số, đuôi số hoặc một chuỗi số nhất định.

## Tính năng

- Tạo, bật/tắt và xóa các nhóm số theo tên bạn chọn.
- Chặn số điện thoại cụ thể hoặc số khớp với mẫu:
  - `0901234567` — khớp chính xác
  - `0247300*` — bắt đầu bằng `0247300`
  - `*4567` — kết thúc bằng `4567`
  - `*7300*` — có chứa `7300`
- Nhận diện hai cách viết phổ biến của số Việt Nam, chẳng hạn `+84901234567` và `0901234567`.
- Xem nhật ký các cuộc gọi đã bị chặn gần đây (tối đa 100 mục).

## Cách hoạt động

Ứng dụng sử dụng dịch vụ sàng lọc cuộc gọi của Android. Để chặn cuộc gọi, bạn cần chọn **Lá Chắn Cuộc Gọi** làm ứng dụng nhận diện và chặn cuộc gọi mặc định trên điện thoại. Android sẽ hiển thị yêu cầu xác nhận khi bạn kích hoạt tính năng này.

Bạn tự thêm số điện thoại và mẫu cần chặn; ứng dụng không tự xác định một số điện thoại có phải là spam hay không. Do Android giới hạn thông tin được cung cấp cho dịch vụ sàng lọc, ứng dụng kiểm tra các cuộc gọi từ số không có trong Danh bạ.

## Quyền riêng tư

- Không cần kết nối Internet; ứng dụng không gửi danh sách số điện thoại lên máy chủ.
- Danh sách chặn và nhật ký trong ứng dụng được lưu trên thiết bị.
- Ứng dụng không yêu cầu quyền đọc Danh bạ.
- Cuộc gọi bị chặn vẫn được giữ trong nhật ký cuộc gọi của Android.

## Tương thích

- Android 10 trở lên.
- Cần đặt ứng dụng làm ứng dụng nhận diện và chặn cuộc gọi để sử dụng tính năng chặn.

## Tải ứng dụng

Mã nguồn của dự án được lưu trong repository này. Nếu có bản cài đặt được phát hành, bạn có thể tìm thấy tại mục [Releases](https://github.com/Tngoclinh/SpamShieldVN/releases).

## Công nghệ

Ứng dụng được phát triển bằng Kotlin, Jetpack Compose và Android Call Screening API.
