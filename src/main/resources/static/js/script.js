// Mobile Menu Toggle
const hamburger = document.getElementById('hamburger');
const mobileMenu = document.getElementById('mobileMenu');
const closeBtn = document.getElementById('closeBtn');
const mobileMenuOverlay = document.getElementById('mobileMenuOverlay');
const mobileNavLinks = document.querySelectorAll('.mobile-nav a');
const header = document.querySelector('.header');

function getHeaderOffset() {
    if (!header) {
        return 80;
    }
    return header.offsetHeight + 16;
}

function scrollToAnchorTarget(target) {
    if (!target) {
        return;
    }

    const elementPosition = target.getBoundingClientRect().top;
    const offsetPosition = elementPosition + window.pageYOffset - getHeaderOffset();

    window.scrollTo({
        top: offsetPosition,
        behavior: 'smooth'
    });
}

function openMobileMenu() {
    mobileMenu.classList.add('active');
    if (mobileMenuOverlay) {
        mobileMenuOverlay.classList.add('active');
    }
    document.body.style.overflow = 'hidden';
}

function closeMobileMenu() {
    mobileMenu.classList.remove('active');
    if (mobileMenuOverlay) {
        mobileMenuOverlay.classList.remove('active');
    }
    document.body.style.overflow = '';
}

// Open mobile menu
hamburger.addEventListener('click', () => {
    openMobileMenu();
});

// Close mobile menu
closeBtn.addEventListener('click', () => {
    closeMobileMenu();
});

// Close menu when clicking on a link
mobileNavLinks.forEach(link => {
    link.addEventListener('click', () => {
        closeMobileMenu();
    });
});

// Close menu when clicking outside
mobileMenu.addEventListener('click', (e) => {
    if (e.target === mobileMenu) {
        closeMobileMenu();
    }
});

if (mobileMenuOverlay) {
    mobileMenuOverlay.addEventListener('click', () => {
        closeMobileMenu();
    });
}

// Back to Top Button
const backToTop = document.getElementById('backToTop');
const footer = document.querySelector('.footer');

function smoothScrollToTop(pixelsPerMs = 1.4) {
    const startY = window.pageYOffset;
    if (startY <= 0) {
        return;
    }
    const startTime = performance.now();
    const duration = Math.max(startY / pixelsPerMs, 600);
    const root = document.documentElement;
    const previousScrollBehavior = root.style.scrollBehavior;

    root.style.scrollBehavior = 'auto';

    function easeInOutCubic(progress) {
        return progress < 0.5
            ? 4 * progress * progress * progress
            : 1 - Math.pow(-2 * progress + 2, 3) / 2;
    }

    function step(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        const easedProgress = easeInOutCubic(progress);

        window.scrollTo(0, startY * (1 - easedProgress));

        if (progress < 1) {
            window.requestAnimationFrame(step);
        } else {
            root.style.scrollBehavior = previousScrollBehavior;
        }
    }

    window.requestAnimationFrame(step);
}

if (backToTop) {
    backToTop.addEventListener('click', () => {
        smoothScrollToTop();
    });
}

// Smooth scrolling for same-page anchor links
document.querySelectorAll('a[href^="#"], a[href^="/#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        const href = this.getAttribute('href');
        if (!href) {
            return;
        }

        let hash = href;
        if (href.startsWith('/#')) {
            if (window.location.pathname !== '/' && window.location.pathname !== '/index.html') {
                return;
            }
            hash = href.substring(1);
        }

        if (!hash.startsWith('#')) {
            return;
        }

        const target = document.querySelector(hash);

        if (target) {
            e.preventDefault();
            scrollToAnchorTarget(target);
        }
    });
});

// Show/hide back to top button based on scroll position
window.addEventListener('scroll', () => {
    if (!backToTop) {
        return;
    }

    if (!footer) {
        if (window.pageYOffset > 300) {
            backToTop.classList.add('is-visible');
        } else {
            backToTop.classList.remove('is-visible');
        }
        return;
    }

    const footerTop = footer.getBoundingClientRect().top;
    const triggerPoint = window.innerHeight * 0.9;

    if (footerTop <= triggerPoint) {
        backToTop.classList.add('is-visible');
    } else {
        backToTop.classList.remove('is-visible');
    }
});

window.addEventListener('load', () => {
    if (!window.location.hash) {
        return;
    }

    const target = document.querySelector(window.location.hash);
    if (target) {
        window.setTimeout(() => {
            scrollToAnchorTarget(target);
        }, 50);
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
  const reservationDateAlert = document.getElementById("reservationDateAlert");
  const reservationDateMessage = document.getElementById("reservationDateMessage");
  const reservationTimeAlert = document.getElementById("reservationTimeAlert");
  const reservationTimeMessage = document.getElementById("reservationTimeMessage");

  if (!reservationDateInput || !reservationTimeSelect) {
    return;
  }

  const initialSelectedTime = reservationTimeSelect.dataset.selectedTime || reservationTimeSelect.value;

  const setMessage = (element, message, alertElement) => {
    if (!element) {
      return;
    }
    element.textContent = message || "";
    if (alertElement) {
      alertElement.classList.toggle("is-visible", Boolean(message));
    }
  };

  const setTimeSelectDisabled = (disabled) => {
    reservationTimeSelect.disabled = disabled;
    reservationTimeSelect.classList.toggle("is-disabled", disabled);
  };

  const buildOption = (value, label, selected = false) => {
    const option = document.createElement("option");
    option.value = value;
    option.textContent = label;
    option.selected = selected;
    return option;
  };

  const resetTimeOptions = (label) => {
    reservationTimeSelect.innerHTML = "";
    reservationTimeSelect.appendChild(buildOption("", label, true));
  };

  const updateTimeOptions = async (selectedTime = "") => {
    const selectedDate = reservationDateInput.value;
    resetTimeOptions("選択してください");
    setMessage(reservationTimeMessage, "", reservationTimeAlert);
    setTimeSelectDisabled(false);

    if (!selectedDate) {
      setMessage(reservationDateMessage, "", reservationDateAlert);
      resetTimeOptions("日付を選択してください");
      setTimeSelectDisabled(true);
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
        setMessage(reservationDateMessage, "木曜日は休診日です。別の日付を選択してください。", reservationDateAlert);
        resetTimeOptions("選択できません");
        setTimeSelectDisabled(true);
      } else if (day === 0) {
        setMessage(reservationDateMessage, "日曜日は休診日です。別の日付を選択してください。", reservationDateAlert);
        resetTimeOptions("選択できません");
        setTimeSelectDisabled(true);
      } else if (day === 6) {
        setMessage(reservationDateMessage, "土曜日は午前のみ診療しています。午後の予約は選べません。", reservationDateAlert);
      } else {
        setMessage(reservationDateMessage, "", reservationDateAlert);
      }

      if (availableSlots.length === 0) {
        setMessage(reservationTimeMessage, "選択した日は予約可能な時間帯がありません。別の日付を選択してください。", reservationTimeAlert);
        resetTimeOptions("選択できません");
        setTimeSelectDisabled(true);
      }
    } catch (error) {
      setMessage(reservationTimeMessage, "予約時間の取得に失敗しました。時間をおいて再度お試しください。", reservationTimeAlert);
      resetTimeOptions("選択できません");
      setTimeSelectDisabled(true);
    }
  };

  reservationDateInput.addEventListener("change", () => {
    updateTimeOptions("");
  });

  updateTimeOptions(initialSelectedTime);
});
