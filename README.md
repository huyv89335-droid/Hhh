# AutoBuilder — Bước 2 (v2.0.0)

Mod Fabric client-side cho **Minecraft 1.21.11**, Java 21.
Đọc `.litematic` / `.schem` (kèm **3 bản vẽ có sẵn trong JAR**), mở bảng bằng phím **K** hoặc lệnh `/autobuilder`, tự xoay camera mượt và đặt block trong tầm với.

**Mới ở bước 2:**
- Chế độ **Creative / Survival / Auto**: Creative tự lấy block từ kho sáng tạo; Survival tự chạy về rương đã cài toạ độ để lấy đúng block thiếu rồi quay lại xây tiếp.
- **Anti-stuck**: kẹt 3 giây tự nhảy, 6 giây tự đi đường vòng, block đặt sai vị trí/sai loại bị đập bỏ.
- **Giàn giáo**: tự bắc scaffolding khi xây trên cao; thứ tự xây **dưới lên trên, trong ra ngoài**; **tuyệt đối không đặt chất lỏng** (nước/lava bị bỏ qua và ghi chú vị trí đặt tay).
- **Tự ăn** khi thanh đói giảm; hiển thị số block còn lại trên action bar; **tự tắt khi đạt 100%**.
- **Không tự tắt khi đang xây**: lỗi tạm thời (thiếu đồ, ngoài tầm với, kẹt) chỉ tạm dừng rồi thử lại.
- **Webhook Discord**: thông báo bắt đầu / hoàn thành / hết đồ / kẹt lâu.

> Chỉ dùng trong singleplayer, LAN hoặc server riêng mà bạn có quyền. Không dùng để né anti-cheat hay phá nội quy server.

## 1. Phiên bản đã ghim

| Thành phần | Phiên bản |
|---|---|
| Minecraft | `1.21.11` |
| Java | `21` |
| Fabric Loader | `0.18.4` tối thiểu |
| Fabric API | `0.141.6+1.21.11` |
| Yarn Mappings | `1.21.11+build.6` |
| Fabric Loom | `1.14-SNAPSHOT` |
| Gradle | `9.2.1` |

Tất cả giá trị nằm trong `gradle.properties`. Không nâng riêng một dòng nếu chưa đổi phiên bản Minecraft.

## 2. Lấy file JAR mà không biết lập trình

### Cách A — GitHub Actions (không cần cài gì trên máy)

1. Tạo một tài khoản GitHub miễn phí.
2. Tạo repository mới, chọn **Add file → Upload files**.
3. Kéo toàn bộ nội dung thư mục dự án vào trình duyệt (giữ đúng cấu trúc thư mục, bao gồm thư mục `.github`).
4. Commit thay đổi.
5. Mở tab **Actions** → workflow **Build AutoBuilder** → **Run workflow**.
6. Khi dấu tích xanh xuất hiện, mở lần chạy mới nhất và tải artifact **autobuilder-jar**.
7. Giải nén artifact, lấy tệp `.jar` trong `build/libs/` đặt vào thư mục `mods`.

Workflow đã dùng sẵn Gradle 9.2.1 và JDK 21, nên không phụ thuộc tệp nhị phân `gradle-wrapper.jar`.

### Cách B — IntelliJ IDEA

1. Cài **IntelliJ IDEA Community** và **JDK 21** (Temurin/Adoptium).
2. Mở thư mục dự án bằng IntelliJ.
3. Chờ Gradle import xong. Nếu IDE hỏi JDK, chọn JDK 21.
4. Mở cửa sổ Gradle, chạy `Tasks → build → build`.
5. Tệp nằm ở `build/libs/autobuilder-1.0.0.jar`.

Nếu máy đã có Gradle 9.2.1:
```bash
gradle build --no-daemon
```

## 3. Cài mod

1. Cài Fabric Loader cho Minecraft **1.21.11**.
2. Tải Fabric API bản **0.141.6+1.21.11**.
3. Đặt `fabric-api-0.141.6+1.21.11.jar` và `autobuilder-1.0.0.jar` vào:
   - Windows: `%appdata%\.minecraft\mods\`
   - macOS: `~/Library/Application Support/minecraft/mods/`
4. Khởi động profile Fabric 1.21.11.
5. Mở **Options → Controls → Key Binds**, tìm mục **AutoBuilder** nếu muốn đổi phím K.

## 4. Chuẩn bị bản vẽ

Tạo thư mục:
```text
.minecraft/
├── mods/
│   ├── fabric-api-0.141.6+1.21.11.jar
│   └── autobuilder-1.0.0.jar
├── schematics/
│   ├── nha-go.litematic
│   └── cong-thanh.schem
└── config/
    └── autobuilder.json   (tự tạo sau lần lưu đầu tiên)
```

- Hỗ trợ Litematica `.litematic` và Sponge Schematic `.schem` (v2/v3 có `BlockData` hoặc `Blocks`).
- Loader chỉ quét tệp ở cấp đầu tiên của `schematics/`, không quét thư mục con.
- Bản vẽ được chuẩn hoá về góc nhỏ nhất; toạ độ X Y Z bạn nhập là **góc đặt công trình**.
- Air, cave_air, void_air và structure_void được bỏ qua.
- Giới hạn an toàn: tối đa 25.000 block không-air và 4.000.000 ô mỗi region.

## 5. Sử dụng (bước 2)

1. Đứng gần vị trí xây. Survival: để block trong túi hoặc trong **rương** đã đặt gần đó.
2. Nhấn **K** (hoặc gõ `/autobuilder`).
3. Chọn bản vẽ ở cột trái — 3 bản vẽ có sẵn luôn nằm đầu danh sách.
4. Nhập X Y Z góc bắt đầu.
5. Nhập X Y Z của rương đồ, bấm **Rương đồ: BẬT**.
6. Chọn **Chế độ**: AUTO (theo game) / SURVIVAL / CREATIVE.
7. Dán URL webhook Discord nếu muốn nhận thông báo (bỏ trống = tắt).
8. Bấm **BẬT TỰ ĐỘNG** rồi đóng GUI (Esc). Số block còn lại hiện trên action bar.

Controller sẽ:
- xây theo thứ tự **dưới lên trên, trong ra ngoài** (không tự nhốt);
- **bỏ qua tuyệt đối** nước/lava/cột bọt khí và ghi chú vị trí cần đặt tay;
- nội suy camera về tâm block, bước ngắn có kiểm tra vật cản, tự leo 1 block;
- hết block: Survival tự **chạy về rương**, QUICK_MOVE đúng loại thiếu, quay lại xây tiếp;
- hết block mà chưa bật rương: **tạm dừng chờ** (không tắt mod) và gửi webhook nếu có;
- mục tiêu cao hơn 3 block: tự **bắc scaffolding** (cần item scaffolding trong hotbar);
- kẹt 3 giây: tự nhảy; 6 giây: đi vòng vuông góc; 15 giây: webhook + nhờ bạn đẩy giúp;
- block đặt sai ô mục tiêu: tự đập (creative đập ngay, survival đào tối đa 5 giây);
- đói dưới ngưỡng (mặc định 14/20): tự chọn thức ăn trong hotbar và ăn;
- đạt **100%**: tự tắt, tổng kết trong chat và gửi webhook ✅.

Mở GUI lại và bấm **TẮT TỰ ĐỘNG** để dừng bất cứ lúc nào. Lỗi tạm thời KHÔNG làm mod tự tắt.

## 5.1 Không hiện menu?

Thứ tự kiểm tra quan trọng hơn việc đoán:

1. **Mods list** có `AutoBuilder` không? Không có = mod chưa được nạp. Kiểm tra Fabric API, Loader `>=0.18.4`, Java 21 và đúng thư mục `mods`.
2. **Options → Controls → Key Binds** có danh mục `AutoBuilder` không? Không có = `ClientModInitializer` không chạy. Đọc lỗi trong `latest.log`.
3. Trong thế giới, thử `K`, sau đó thử lệnh **`/autobuilder`** hoặc **`/ab`**. Lệnh dự phòng có từ bản vá này.
4. Trong `latest.log` phải có:
   - `AutoBuilder menu key registered`
   - `AutoBuilder initialized`
   - `AutoBuilder joined world`
5. Nếu tên danh mục hiện khóa thô `key.category.autobuilder.main`, hãy cập nhật hai file ngôn ngữ. 1.21.11 dùng `key.category.autobuilder.main` (số ít), không phải `key.categories...`.
6. Nếu danh sách bản vẽ trống: đặt `.litematic`/`.schem` trực tiếp trong `.minecraft/schematics/`, không dùng thư mục con, rồi bấm **Quét lại**.
7. Trên PojavLauncher/điện thoại, bàn phím ảo có thể không gửi phím chữ. Đổi keybinding sang phím có sẵn trên bàn phím ảo hoặc dùng `/autobuilder`.

Bản vá cũng đăng ký phím **trước** khi đọc config/schematic. Nếu file bản vẽ lỗi, menu vẫn mở và lỗi được liệt kê trong GUI.

## 5.2 Webhook Discord

1. Trong Discord: **Server Settings → Integrations → Webhooks → New Webhook**, chép URL (bắt đầu bằng `https://discord.com/api/webhooks/...`).
2. Dán vào ô **WEBHOOK DISCORD** trong GUI rồi đóng GUI (config tự lưu).
3. Sự kiện được gửi: 🏗️ bắt đầu xây · 🔄 về rương lấy đồ · 📦 hết đồ trong rương/chưa cấu hình rương · ⚠️ kẹt quá 15 giây · ✅ hoàn thành 100%.

Webhook chạy ở thread nền riêng, chỉ nhận URL `https://`, không bao giờ chặn game. Xoá nội dung ô để tắt.

## 5.3 Bản vẽ có sẵn (không cần file)

Trong JAR có sẵn 3 bản vẽ, luôn xuất hiện đầu danh sách kể cả khi thư mục `schematics/` trống:

| Tên | Nội dung | Lưu ý |
|---|---|---|
| [Có sẵn] Farm đá cuội v2 | Máy farm đá cuội + rương gom + đuốc | Mod xây khung; **tự đặt 1 xô nước + 1 xô lava** vào 2 ô được ghi chú |
| [Có sẵn] Kho chứa đồ v2 | Sàn 9×7, 7 rương, đuốc, mái kín | Rương trống để bạn nạp vật liệu cho bước 2 |
| [Có sẵn] Nhà khởi động v2 | Nhà 7×7, bàn chế tạo, lò nung, 2 rương, cửa kính | Giường đặt tay (block 2 phần chưa hỗ trợ) |

Sửa hoặc thêm bản vẽ có sẵn trong `BundledSchematics.java`.

## 6. Vì sao tiến trình dừng?

| Thông báo | Cách xử lý |
|---|---|
| Chưa chọn bản vẽ | Bấm **Quét lại**, chọn một tệp |
| Ngoài tầm với | Đứng gần hơn 4.3 block; kiểm tra bật "Tới gần" |
| Không có block trong hotbar | Đặt item đúng loại vào một trong 9 ô hotbar |
| Không tìm được mặt đỡ | Đặt một block nền gần vị trí, hoặc bắt đầu từ nền đất |
| Đang ở server | Tự tới gần bị tắt ngoài singleplayer/LAN |
| Màn hình khác đang mở | Đóng inventory/chat để controller tick |

## 7. Giới hạn đã biết (bước 2)

- Chưa xoay, mirror hay scale bản vẽ.
- Tự lấy đồ mới hỗ trợ **rương đơn/rương đôi**; chưa hỗ trợ shulker, ender chest, barrel.
- Di chuyển dùng bước nhỏ phía client: trên server có anti-cheat nghiêm ngặt, việc chạy về rương có thể bị kéo về (rubber-band). Singleplayer/LAN hoạt động ổn định nhất.
- Giàn giáo cần **item scaffolding trong hotbar**; hết scaffolding mod sẽ chờ và báo.
- Chưa đặt chính xác hướng cho block có hướng (cầu thang, dispenser...) — đặt theo state trong bản vẽ, mặt đỡ chọn tự động.
- Block entity (rương, lò, bảng) chỉ đặt khối cơ bản, không khôi phục NBT bên trong.
- Chất lỏng (nước/lava) **không bao giờ tự đặt** — kể cả khi bản vẽ có; vị trí được ghi chú để đặt tay.
- Không dùng trên server nơi tự động hoá bị cấm.

## 8. Cấu trúc

```text
autobuilder/
├── build.gradle
├── gradle.properties
├── settings.gradle
├── gradle/wrapper/gradle-wrapper.properties
├── .github/workflows/build.yml
├── README.md
└── src/main/
    ├── java/com/autobuilder/
    │   ├── AutoBuilder.java
    │   ├── AutoBuilderClient.java
    │   ├── config/AutoBuilderConfig.java
    │   ├── schematic/Schematic.java
    │   ├── schematic/SchematicLoader.java
    │   ├── schematic/BlockStateParser.java
    │   ├── schematic/BundledSchematics.java   (mới bước 2)
    │   └── client/
    │       ├── BuildController.java
    │       ├── AutoBuilderScreen.java
    │       └── WebhookNotifier.java           (mới bước 2)
    └── resources/
        ├── fabric.mod.json
        └── assets/autobuilder/lang/{en_us.json, vi_vn.json}
```

## 9. Ghi chú kỹ thuật (bước 2)

- Chỉ đăng ký `ClientModInitializer`; không có server entrypoint.
- Không dùng Mixin, không sửa packet; mọi thao tác qua `interactionManager` như người chơi thật.
- Lấy đồ từ rương dùng `clickSlot(..., QUICK_MOVE, ...)` hợp lệ trên container đang mở.
- Creative cấp item bằng `clickCreativeStack` (server chấp nhận ở chế độ Creative).
- Thức ăn nhận diện qua data component `DataComponentTypes.FOOD` (API 1.21.2+).
- Anti-stuck theo dõi độ dịch chuyển ngang mỗi tick; nhảy bằng vận tốc 0.42 khi chạm đất.
- Webhook dùng `java.net.http.HttpClient` ở thread daemon riêng, timeout 8 giây.
- Camera được cập nhật mỗi client tick với bước tối đa 12°/tick, không can thiệp render loop.
- GUI chỉ dùng `DrawContext.fill` và chữ; không có khung 3D/texture động.
- Config được lưu bằng Gson vào `config/autobuilder.json`.

## 10. Giấy phép

Mã nguồn minh hoạ cho mục đích học tập và sử dụng cá nhân theo giấy phép MIT.
Minecraft là thương hiệu của Mojang Studios. Dự án không liên kết chính thức với Mojang, Microsoft hay FabricMC.