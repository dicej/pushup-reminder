.PHONY: build
build: reminder

.PHONY: reminder
reminder:
	make -f app.mk \
		name=reminder \
		data-files="data/icon.png" \
		sources="$(shell find src -name '*.java')" \
		source-directory=src \
		main-class=PushupReminder


.PHONY: clean
clean:
	rm -rf build
