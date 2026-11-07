export default {
  mounted(el, binding) {
    el.src = ""; // 初始時確保 src 為空

    const loadImage = () => {
      const src = binding.value;
      if (!src) return;
      el.src = src;
      el.dataset.loaded = "true";
      observer.unobserve(el);
    };

    const observer = new IntersectionObserver(
      (entries, observer) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting && !el.dataset.loaded) {
            loadImage();
          }
        });
      },
      {
        root: null, // 預設為視口
        rootMargin: "10px", // 只在快要進入視口時觸發
        threshold: 0.1, // 當圖片 10% 可見時觸發
      }
    );

    observer.observe(el);
  }
};