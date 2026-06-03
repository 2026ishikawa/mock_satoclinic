// Mobile Menu Toggle
const hamburger = document.getElementById('hamburger');
const mobileMenu = document.getElementById('mobileMenu');
const closeBtn = document.getElementById('closeBtn');
const mobileNavLinks = document.querySelectorAll('.mobile-nav a');

// Open mobile menu
hamburger.addEventListener('click', () => {
    mobileMenu.classList.add('active');
    document.body.style.overflow = 'hidden';
});

// Close mobile menu
closeBtn.addEventListener('click', () => {
    mobileMenu.classList.remove('active');
    document.body.style.overflow = '';
});

// Close menu when clicking on a link
mobileNavLinks.forEach(link => {
    link.addEventListener('click', () => {
        mobileMenu.classList.remove('active');
        document.body.style.overflow = '';
    });
});

// Close menu when clicking outside
mobileMenu.addEventListener('click', (e) => {
    if (e.target === mobileMenu) {
        mobileMenu.classList.remove('active');
        document.body.style.overflow = '';
    }
});

// Back to Top Button
const backToTop = document.getElementById('backToTop');

backToTop.addEventListener('click', () => {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
});

// Smooth scrolling for all anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));

        if (target) {
            const headerOffset = 80;
            const elementPosition = target.getBoundingClientRect().top;
            const offsetPosition = elementPosition + window.pageYOffset - headerOffset;

            window.scrollTo({
                top: offsetPosition,
                behavior: 'smooth'
            });
        }
    });
});

// Show/hide back to top button based on scroll position
window.addEventListener('scroll', () => {
    if (window.pageYOffset > 300) {
        backToTop.style.opacity = '1';
    } else {
        backToTop.style.opacity = '0.7';
    }
});



// faq アコーディオン　↓

document.addEventListener("DOMContentLoaded", () => {
  const accordions = document.querySelectorAll(".accordion");

  accordions.forEach((accordion) => {
    const button = accordion.querySelector(".accordion-button");
    const content = accordion.querySelector(".accordion-content");

    button.addEventListener("click", () => {
      const isOpen = accordion.classList.contains("is-open");

      if (isOpen) {
        accordion.classList.remove("is-open");
        content.style.maxHeight = null;
      } else {
        accordion.classList.add("is-open");
        content.style.maxHeight = content.scrollHeight + "px";
      }
    });
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const reservationDateInput = document.getElementById("reservationDate");
  const reservationTimeSelect = document.getElementById("reservationTime");
  const reservationDateMessage = document.getElementById("reservationDateMessage");
  const reservationTimeMessage = document.getElementById("reservationTimeMessage");

  if (!reservationDateInput || !reservationTimeSelect) {
    return;
  }

  const initialSelectedTime = reservationTimeSelect.dataset.selectedTime || reservationTimeSelect.value;

  const setMessage = (element, message) => {
    if (!element) {
      return;
    }
    element.textContent = message || "";
  };

  const buildOption = (value, label, selected = false) => {
    const option = document.createElement("option");
    option.value = value;
    option.textContent = label;
    option.selected = selected;
    return option;
  };

  const updateTimeOptions = async (selectedTime = "") => {
    const selectedDate = reservationDateInput.value;
    reservationTimeSelect.innerHTML = "";
    reservationTimeSelect.appendChild(buildOption("", "選択してください", !selectedTime));
    setMessage(reservationTimeMessage, "");

    if (!selectedDate) {
      setMessage(reservationDateMessage, "");
      return;
    }

    try {
      const response = await fetch(`/api/reservation-slots?date=${selectedDate}`);
      if (!response.ok) {
        throw new Error("failed to load slots");
      }
      const data = await response.json();
      const availableSlots = data.slots.filter((slot) => slot.available);

      availableSlots.forEach((slot) => {
        reservationTimeSelect.appendChild(buildOption(slot.time, `${slot.time}（残り${slot.remaining}枠）`, slot.time === selectedTime));
      });

      const selectedDay = new Date(`${selectedDate}T00:00:00`);
      const day = selectedDay.getDay();
      if (day === 4) {
        setMessage(reservationDateMessage, "木曜日は休診のため予約できません。");
      } else if (day === 0) {
        setMessage(reservationDateMessage, "日曜日は休診のため予約できません。");
      } else if (day === 6) {
        setMessage(reservationDateMessage, "土曜日は午前のみ予約できます。");
      } else {
        setMessage(reservationDateMessage, "");
      }

      if (availableSlots.length === 0) {
        setMessage(reservationTimeMessage, "選択した日は予約可能な時間帯がありません。");
      }
    } catch (error) {
      setMessage(reservationTimeMessage, "予約時間の取得に失敗しました。時間をおいて再度お試しください。");
    }
  };

  reservationDateInput.addEventListener("change", () => {
    updateTimeOptions("");
  });

  updateTimeOptions(initialSelectedTime);
});
