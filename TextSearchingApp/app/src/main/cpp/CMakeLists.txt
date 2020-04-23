
cmake_minimum_required(VERSION 3.4.1)

# Now build app's shared lib
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11 -Werror -Wall -Wno-unused-function")

add_library(log_reader SHARED
        ${CMAKE_CURRENT_SOURCE_DIR}/log_reader.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/jni_utils.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/log_reader_jni.cpp
        )

# Include libraries
target_link_libraries(
        log_reader
        android
        log)
