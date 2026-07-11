
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.18.1)

project("kernelsu")

find_package(cxx REQUIRED CONFIG)
link_libraries(cxx::cxx)

add_library(kernelsu
        SHARED
        jni.cc
        ksu.cc
        )

target_include_directories(kernelsu PRIVATE .)

find_library(log-lib log)

target_link_libraries(kernelsu ${log-lib})

add_library(adbroot SHARED adbroot.cc)
target_link_libraries(adbroot ${log-lib})

if (ANDROID_ABI STREQUAL "arm64-v8a")
    set(GHOSTLOCK_DIR "${CMAKE_CURRENT_SOURCE_DIR}/ghostlock")

    function(add_ghostlock_target target_name project_name)
        set(target_dir "${GHOSTLOCK_DIR}/src/targets/${project_name}")

        add_library(${target_name}
                SHARED
                "${GHOSTLOCK_DIR}/src/main.c"
                "${target_dir}/util.c"
                "${target_dir}/slide.c"
                "${target_dir}/fops.c"
                "${GHOSTLOCK_DIR}/src/pipe.c"
                "${GHOSTLOCK_DIR}/src/root.c"
                "${GHOSTLOCK_DIR}/src/preload.c"
                )

        target_include_directories(${target_name} PRIVATE "${GHOSTLOCK_DIR}/src")
        target_compile_definitions(${target_name}
                PRIVATE
                FAST_MODE=1
                FAST_VERIFY=1
                TARGET_CONFIG_H=\"targets/${project_name}/target.h\"
                )
        target_compile_options(${target_name}
                PRIVATE
                -Wno-unused-parameter
                -Wno-sign-compare
                -Wno-unused-function
                )
        target_link_libraries(${target_name} ${log-lib})
    endfunction()

    add_ghostlock_target(ghostlock_dali_ap3a dali-AP3A.240905.015.A2)
    add_ghostlock_target(ghostlock_dali_bp2a dali-BP2A.250605.031.A3)
endif()
