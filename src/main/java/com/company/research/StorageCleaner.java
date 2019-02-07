package com.company.research;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.mutable.MutableLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StorageCleaner {

    private static final String storeFolder = "D:/local_storage";

    public static void main(String[] args) {
        new StorageCleaner().run();
    }

    private void run() {
        onCleanStorage(StorageRegistry.CURRENCY_BALANCES);
//
//        int d1 = Ints.checkedCast(TimeUnit.DAYS.toMillis(1));
//        int d2 = 24 * 60 * 60 * 1000;
//        System.out.println("d1 = " + d1);
//        System.out.println("d2 = " + d2);
//        System.out.println(System.currentTimeMillis());
    }


    private void onCleanStorage(StorageRegistry storage) {
        @Nonnull StorageRegistry.CleaningRules cleaningRules = storage.getCleaningRules();
        File storageFolder = getStorageDir(storage);

        if (storageFolder.exists()) {
            Preconditions.checkState(storageFolder.isDirectory());

            List<File> filesListOrderedByLastModified = getFiles(storage)
                    .sorted(Comparator.comparingLong(File::lastModified))
                    .collect(Collectors.toList());

            List<File> deletedFiles = new ArrayList<>();

            if (cleaningRules.hasFilesLifetimeRestriction()) {
                long expirationTime = TimeUtils.getMillis() - cleaningRules.getFilesLifetimeDays() * TimeUnit.DAYS.toMillis(1);

                //сначала удаляем устаревшие файлы
                filesListOrderedByLastModified.stream()
                        .filter(file -> file.lastModified() < expirationTime)
                        .filter(File::delete)
                        .forEach(deletedFiles::add);
            }

            if (cleaningRules.hasFilesLimitRestriction()) {
                //затем лишние (если превышен лимит файлов)
                filesListOrderedByLastModified.stream()
                        .filter(file -> !deletedFiles.contains(file))
                        .sorted(Comparator.comparingLong(File::lastModified).reversed())    //чтобы скипнуть самые свежие файлы
                        .skip(cleaningRules.getMaxFilesLimit())
                        .filter(File::delete)
                        .forEach(deletedFiles::add);
            }

            if (cleaningRules.hasStorageSizeRestriction()) {

                long maxStorageSize = cleaningRules.getMaxStorageSizeMB() * 1024 * 1024;
                MutableLong currentStorageSize = new MutableLong(FileUtils.sizeOfDirectory(storageFolder));

                if (currentStorageSize.longValue() > maxStorageSize) {

                    //и только потом если нужно удаляем файлы начиная с самых старых до тех пор пока нас не удовлетворит размер хранилища
                    filesListOrderedByLastModified.stream()
                            .filter(file -> !deletedFiles.contains(file))
                            .filter(file -> {
                                if (currentStorageSize.longValue() <= maxStorageSize) {
                                    return false;
                                }

                                long fileSize = file.length();
                                boolean deleted = file.delete();
                                if (deleted) {
                                    currentStorageSize.subtract(fileSize);
                                }

                                return deleted;
                            })
                            .forEach(deletedFiles::add);
                }

            }

            System.out.println(String.format("Storage[%s] successfully cleaned! Deleted %s files", storage.name(), deletedFiles.size()));
        }
    }

    public Stream<File> getFiles(StorageRegistry storage) {
        return Optional.ofNullable(getStorageDir(storage).listFiles())
                .map(files -> Arrays.stream(files).filter(File::isFile))
                .orElseGet(Stream::empty);
    }

    private File getStorageDir(@Nonnull StorageRegistry storage) {
        return new File(storeFolder, storage.getPath());
    }

    @Getter
    @RequiredArgsConstructor
    enum StorageRegistry {

        CURRENCY_BALANCES("currency_balances", 10, 5, 30),
        ITEM_BALANCES("items_balances", null, null, 30);

        StorageRegistry(String path, Integer maxFilesCount, Integer maxFolderSizeMB, Integer filesLifetimeDays) {
            this(path);
            this.cleaningRules = new CleaningRules(maxFilesCount, maxFolderSizeMB, filesLifetimeDays);
        }

        private final String path;
        @Nullable private CleaningRules cleaningRules;

        public boolean hasCleaningRules() {
            return cleaningRules != null;
        }

        @Value
        public class CleaningRules {
            @Nullable Integer maxFilesLimit;
            @Nullable Integer maxStorageSizeMB;
            @Nullable Integer filesLifetimeDays;

            public boolean hasFilesLimitRestriction() {
                return maxFilesLimit != null;
            }

            public boolean hasStorageSizeRestriction() {
                return maxStorageSizeMB != null;
            }

            public boolean hasFilesLifetimeRestriction() {
                return filesLifetimeDays != null;
            }
        }
    }

    static class TimeUtils {

        public static long getMillis() {
            return System.currentTimeMillis();
        }
    }

}