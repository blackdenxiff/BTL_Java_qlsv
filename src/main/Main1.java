public void remove(Item target) {
    // 1. Kiểm tra danh sách rỗng
    if (isEmpty()) {
        return;
    }

    // 2. Xử lý trường hợp item cần xóa nằm ngay node đầu tiên (head)
    if (first.item.equals(target)) {
        first = first.next;
        n--;
        return;
    }

    // 3. Dùng 2 con trỏ để duyệt: 'previous' đi ngay sau 'current'
    Node<Item> previous = first;
    Node<Item> current = first.next;

    while (current != null) {
        // Kiểm tra xem node 'current' có chứa dữ liệu cần xóa không
        if (current.item.equals(target)) {
            // Ngắt kết nối node 'current' bằng cách nối 'previous' với node liền sau 'current'
            previous.next = current.next;
            n--;
            return; // Thoát ngay sau khi xóa thành công 1 phần tử
        }

        // Di chuyển cả 2 con trỏ tiến lên một bước
        previous = current;
        current = current.next;
    }
}