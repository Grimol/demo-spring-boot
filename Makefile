# -------- En fonction de l'os --------
ifeq ($(OS),Windows_NT)
  SHELL := cmd.exe
  .SHELLFLAGS := /Q /C
  MVNW := mvnw.cmd
  MVN  := mvn.cmd
  NULL := nul
else
  SHELL := /bin/sh
  MVNW := ./mvnw
  MVN  := mvn
  NULL := /dev/null
endif

# If Maven Wrapper exists, use it, else fallback to system Maven
ifneq ($(wildcard $(MVNW)),)
  MVN_CMD := $(MVNW)
else
  MVN_CMD := $(MVN)
endif

APP_NAME ?= demo-spring
IMAGE ?= $(APP_NAME)
TAG ?= latest
PORT ?= 8080

.PHONY: help run test build clean docker-build docker-run docker-stop docker-shell version

help:
	@echo "Commandes :"
	@echo "  make run            - Lance l'app en local (Spring Boot)"
	@echo "  make test           - Lance les tests"
	@echo "  make build          - Compile et package (jar, skip tests)"
	@echo "  make clean          - Nettoie le projet"
	@echo "  make docker-build   - Construit l'image Docker $(IMAGE):$(TAG)"
	@echo "  make docker-run     - Lance le conteneur sur le port $(PORT)"
	@echo "  make docker-stop    - Stoppe le conteneur s'il existe"
	@echo "  make docker-shell   - Ouvre un shell dans l'image"
	@echo "  make version        - Affiche les versions Java/Maven"

run:
	$(MVN_CMD) spring-boot:run

test:
	$(MVN_CMD) -q test

build:
	$(MVN_CMD) -DskipTests package

clean:
	$(MVN_CMD) clean

docker-build:
	docker build -t $(IMAGE):$(TAG) .

docker-run:
	- docker rm -f $(APP_NAME) 1>$(NULL) 2>&1
	docker run --name $(APP_NAME) --rm -p $(PORT):8080 $(IMAGE):$(TAG)

docker-stop:
	- docker rm -f $(APP_NAME) 1>$(NULL) 2>&1

docker-shell:
	docker run --rm -it --entrypoint sh $(IMAGE):$(TAG)

version:
	@echo ==== Java ==== && java -version || exit 0
	@echo ==== Maven ==== && $(MVN) -v || exit 0
