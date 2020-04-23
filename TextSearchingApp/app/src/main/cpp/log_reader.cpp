#include "log_reader.h"

LogReader::LogReader()
{

}

LogReader::~LogReader()
{

}

bool LogReader::setFilter(const std::string& filter)
{
    mFilter = filter;
    return true;
}

bool LogReader::readLine(const std::string& line)
{
    return matchesFilter(line);
}

bool LogReader::matchesFilter(const std::string& string)
{
    // TODO: implement search by mask...
    return string.find(mFilter) != std::string::npos;
}
