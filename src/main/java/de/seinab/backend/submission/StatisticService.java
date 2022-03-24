package de.seinab.backend.submission;

import de.seinab.backend.submission.entities.CountStatistic;
import de.seinab.backend.submission.entities.CountStatisticAbbreviated;
import de.seinab.backend.submission.models.BackendSubmission;
import de.seinab.backend.submission.repositories.CountStatisticRepository;
import de.seinab.form.entities.Submission;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticService {

    @Autowired
    private CountStatisticRepository countStatisticRepository;

    public List<CountStatistic> getCountStatistics(Long formId, List<BackendSubmission> submissions) {
        List<CountStatistic> countStatistics = countStatisticRepository.getAllByFormId(formId);
        countStatistics.forEach(countStatistic -> {
            if(countStatistic instanceof CountStatisticAbbreviated) {
                fillAbbreviatedCountStatistic((CountStatisticAbbreviated) countStatistic, submissions);
            } else {
                fillCountStatistic(countStatistic, submissions);
            }
        });
        return countStatistics;
    }

    private void fillAbbreviatedCountStatistic(CountStatisticAbbreviated countStatisticAbbreviated, List<BackendSubmission> submissions) {
        long count = submissions.stream()
                .map(BackendSubmission::getAbbreviatedInputValueList)
                .flatMap(List::stream)
                .filter(inputValue -> inputValue.getAbbreviatedInput().getId().equals(countStatisticAbbreviated.getAbbreviatedKeyInput().getId()))
                .filter(inputValue -> StringUtils.equals(countStatisticAbbreviated.getKeyData(), inputValue.getValue()))
                .count();
        countStatisticAbbreviated.setCount(count);
    }

    private void fillCountStatistic(CountStatistic countStatistic, List<BackendSubmission> submissions) {
        long count = submissions.stream()
                .map(Submission::getInputValueList)
                .flatMap(List::stream)
                .filter(inputValue -> inputValue.getInput().getId().equals(countStatistic.getKeyInput().getId()))
                .filter(inputValue -> StringUtils.equals(countStatistic.getKeyData(), inputValue.getData()))
                .count();
        countStatistic.setCount(count);
    }

}
