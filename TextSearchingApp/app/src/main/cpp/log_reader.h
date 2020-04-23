#ifndef CLOG_READER_H
#define CLOG_READER_H

#include <stddef.h>
#include <string>

class LogReader final
{
public:

    LogReader();
    ~LogReader();

   bool	setFilter(const std::string& filter);

   bool	readLine(const std::string& line);

private:

    bool matchesFilter(const std::string& string);

    std::string mFilter;

};

#endif
