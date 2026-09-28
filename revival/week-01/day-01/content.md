# Bài tập Java cơ bản

## 1. FizzBuzz

Viết chương trình FizzBuzz thuần Java, không tra cứu, không AI.

### Yêu cầu

- In số từ 1 đến 100
- Nếu số chia hết cho 3 thì in: "Fizz"
- Nếu số chia hết cho 5 thì in: "Buzz"
- Nếu số chia hết cho cả 3 và 5 thì in: "FizzBuzz"

---

## 2. Kiểm tra số nguyên tố

Viết chương trình đọc một số nguyên n từ bàn phím bằng `Scanner`, rồi in ra n là số nguyên tố hoặc n không phải số nguyên tố.

### Yêu cầu cấu trúc

- Một hàm riêng `static boolean isPrime(int n)` chỉ trả về đúng/sai, không in gì bên trong.
- `main` lo việc đọc input và in kết quả.

---

## 3. Đếm tần suất từ trong chuỗi

Cho chuỗi: `"the cat and the dog and the bird"`

Viết chương trình:

- Đếm số lần xuất hiện của mỗi từ bằng `Map<String, Integer>`
- In ra 3 từ xuất hiện nhiều nhất, theo thứ tự giảm dần