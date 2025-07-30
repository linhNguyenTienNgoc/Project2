# 🚀 Hướng Dẫn Tối Ưu IntelliJ IDEA - Coffee Shop Management

## 📊 Tình Trạng Hiện Tại

IntelliJ IDEA có thể chạy chậm nếu không được cấu hình đúng cách. Hướng dẫn này sẽ giúp bạn tối ưu IntelliJ để sử dụng nhiều tài nguyên hơn và chạy mượt mà hơn.

## 🔧 Các Bước Tối Ưu IntelliJ

### **Bước 1: Tăng Memory cho IntelliJ**

#### **Cách 1: Sử dụng file cấu hình (Khuyến nghị)**

1. **Tìm file cấu hình IntelliJ:**
   ```
   C:\Users\[YourUsername]\AppData\Roaming\JetBrains\IntelliJIdea[Version]\idea64.exe.vmoptions
   ```

2. **Thay thế nội dung bằng cấu hình tối ưu:**
   - Copy nội dung từ file `intellij-performance-config.vmoptions`
   - Paste vào file `idea64.exe.vmoptions`
   - Restart IntelliJ

#### **Cách 2: Cấu hình trong IntelliJ**

1. **Mở Settings:** `File > Settings` (hoặc `Ctrl+Alt+S`)
2. **Đi đến:** `Build, Execution, Deployment > Compiler`
3. **Tăng các giá trị:**
   - **Build process heap size:** `2048`
   - **Parallel compilation:** `4 threads`

### **Bước 2: Tối Ưu Project Settings**

#### **Maven Settings:**
1. **Maven > Importing:**
   - ✅ **Import Maven projects automatically**
   - ✅ **Download sources and documentation**
   - **VM options for importer:** `-Xmx2048m`

2. **Maven > Runner:**
   - **VM options:** `-Xmx2048m -XX:+UseG1GC`
   - **Parallel builds:** `4 threads`

#### **Compiler Settings:**
1. **Build, Execution, Deployment > Compiler:**
   - **Build process heap size:** `2048`
   - **Parallel compilation:** `4 threads`
   - ✅ **Compile independent modules in parallel**

### **Bước 3: Tối Ưu IntelliJ Features**

#### **Disable Heavy Features:**
1. **Settings > Appearance & Behavior > System Settings:**
   - ❌ **Synchronization on frame activation**
   - ❌ **Save files on focus change**
   - ❌ **Save files automatically**
   - ✅ **Save files if idle for 30 seconds**

2. **Settings > Editor > General > Auto Import:**
   - ✅ **Add unambiguous imports on the fly**
   - ✅ **Optimize imports on the fly**

3. **Settings > Editor > General > Code Completion:**
   - **Case sensitive completion:** `None`
   - **Show suggestions as you type:** `500ms`

#### **Disable Unnecessary Plugins:**
1. **Settings > Plugins:**
   - ❌ Disable plugins không cần thiết:
     - Database tools (nếu không dùng)
     - Version control (nếu không dùng Git)
     - Web development tools
     - Android tools

### **Bước 4: Tối Ưu Project Structure**

#### **Mark Directories:**
1. **Project Structure > Modules:**
   - **src/main/java:** Mark as Sources
   - **src/main/resources:** Mark as Resources
   - **src/test/java:** Mark as Test Sources
   - **target:** Mark as Excluded

#### **Exclude Unnecessary Files:**
1. **Settings > Editor > File Types:**
   - Add `*.log` to Ignored Files and Folders
   - Add `target/` to Ignored Files and Folders

### **Bước 5: Tối Ưu Run Configuration**

#### **Tạo Run Configuration tối ưu:**
1. **Run > Edit Configurations**
2. **Add New Configuration > Application**
3. **Cấu hình:**
   ```
   Name: CoffeeShopApplication (Optimized)
   Main class: com.coffeeshop.shopcoffeemanagement.CoffeeShopApplication
   VM options: -Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -XX:+OptimizeStringConcat -XX:+UseCompressedOops -XX:+UseCompressedClassPointers --add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED --add-opens=javafx.controls/javafx.scene.control=ALL-UNNAMED -Djavafx.css.validation=false -Dprism.order=hw -Dprism.vsync=false
   Working directory: $MODULE_WORKING_DIR$
   ```

## 🎯 Cấu Hình Memory Chi Tiết

### **Cho máy 8GB RAM:**
```
-Xms1024m
-Xmx2048m
-XX:ReservedCodeCacheSize=256m
```

### **Cho máy 16GB RAM:**
```
-Xms2048m
-Xmx4096m
-XX:ReservedCodeCacheSize=512m
```

### **Cho máy 32GB RAM:**
```
-Xms4096m
-Xmx8192m
-XX:ReservedCodeCacheSize=1024m
```

## 🔍 Monitoring Performance

### **Kiểm tra Memory Usage:**
1. **Help > About:** Xem memory usage
2. **Help > Diagnostic Tools > Memory Usage:** Monitor real-time

### **Performance Indicators:**
- 🟢 **Green:** Memory < 70%
- 🟡 **Yellow:** Memory 70-85%
- 🔴 **Red:** Memory > 85%

## 🛠️ Troubleshooting

### **Nếu IntelliJ vẫn chậm:**

1. **Invalidate Caches:**
   - `File > Invalidate Caches and Restart`

2. **Tăng Memory hơn nữa:**
   - Tăng `-Xmx` lên 50% RAM máy

3. **Disable Indexing:**
   - `File > Settings > Appearance & Behavior > System Settings`
   - ❌ **Synchronization on frame activation**

4. **Close Unused Projects:**
   - Chỉ mở project cần thiết

### **Nếu có OutOfMemoryError:**

1. **Tăng heap size:**
   ```
   -Xmx4096m
   ```

2. **Enable GC logging:**
   ```
   -XX:+PrintGCDetails
   -XX:+PrintGCTimeStamps
   ```

## 📋 Checklist Tối Ưu

- [x] Tăng IntelliJ memory
- [x] Tối ưu Maven settings
- [x] Disable heavy features
- [x] Exclude unnecessary files
- [x] Tối ưu run configuration
- [x] Disable unused plugins
- [x] Mark directories correctly
- [x] Enable parallel compilation

## 🎉 Kết Quả Mong Đợi

### **Trước khi tối ưu:**
- ❌ IntelliJ chậm
- ❌ Build time lâu
- ❌ Memory usage cao
- ❌ Lag khi typing
- ❌ Indexing chậm

### **Sau khi tối ưu:**
- ✅ IntelliJ mượt mà
- ✅ Build time nhanh
- ✅ Memory usage ổn định
- ✅ Typing responsive
- ✅ Indexing nhanh

## 🚀 Quick Start

1. **Copy cấu hình:** Copy nội dung `intellij-performance-config.vmoptions`
2. **Paste vào:** `idea64.exe.vmoptions`
3. **Restart IntelliJ**
4. **Tạo run configuration tối ưu**
5. **Test performance**

**🎯 Với các tối ưu này, IntelliJ sẽ chạy mượt mà hơn đáng kể và ứng dụng JavaFX sẽ có hiệu suất tốt hơn!** 